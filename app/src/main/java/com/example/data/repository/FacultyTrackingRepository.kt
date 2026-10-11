package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.db.DatabaseInitializer
import com.example.data.model.ActivityLog
import com.example.data.model.AdminDashboardStats
import com.example.data.model.AvailabilityStatus
import com.example.data.model.Building
import com.example.data.model.Department
import com.example.data.model.FacultyDetailedItem
import com.example.data.model.FacultyLocation
import com.example.data.model.FacultyPrivacy
import com.example.data.model.LocationFreshness
import com.example.data.model.Room
import com.example.data.model.RoomWithBuilding
import com.example.data.model.TimetableEntry
import com.example.data.model.TimetableWithRoom
import com.example.data.model.User
import com.example.data.model.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class FacultyTrackingRepository(private val database: AppDatabase) {

    private val userDao = database.userDao()
    private val deptDao = database.departmentDao()
    private val campusDao = database.campusDao()
    private val locationDao = database.facultyLocationDao()
    private val privacyDao = database.facultyPrivacyDao()
    private val timetableDao = database.timetableDao()
    private val logDao = database.activityLogDao()

    suspend fun checkAndInitializeDatabase() = withContext(Dispatchers.IO) {
        val existingUsers = userDao.getAllUsers().first()
        if (existingUsers.isEmpty()) {
            DatabaseInitializer.populateInitialData(database)
        }
    }

    private data class CampusEntities(
        val users: List<User>,
        val departments: List<Department>,
        val buildings: List<Building>,
        val rooms: List<Room>
    )

    private data class FacultyActivities(
        val locations: List<FacultyLocation>,
        val privacies: List<FacultyPrivacy>,
        val timetables: List<TimetableEntry>
    )

    // 1. Reactive stream of all faculty with full details for students & general viewing
    fun getAllFacultyDetailed(): Flow<List<FacultyDetailedItem>> {
        val campusFlow = combine(
            userDao.getUsersByRole(UserRole.FACULTY.name),
            deptDao.getAllDepartments(),
            campusDao.getAllBuildings(),
            campusDao.getAllRooms()
        ) { users, depts, buildings, rooms ->
            CampusEntities(users, depts, buildings, rooms)
        }

        val activitiesFlow = combine(
            locationDao.getAllLocations(),
            privacyDao.getAllPrivacySettings(),
            timetableDao.getAllTimetableEntries()
        ) { locations, privacies, timetables ->
            FacultyActivities(locations, privacies, timetables)
        }

        return combine(campusFlow, activitiesFlow) { campus, activities ->
            val now = System.currentTimeMillis()
            val deptMap = campus.departments.associateBy { it.id }
            val buildingMap = campus.buildings.associateBy { it.id }
            val roomMap = campus.rooms.associateBy { it.id }
            val privacyMap = activities.privacies.associateBy { it.facultyId }

            val latestLocations = activities.locations.groupBy { it.facultyId }.mapValues { (_, locs) ->
                locs.maxByOrNull { it.updatedAt }
            }

            campus.users.map { faculty ->
                val latestLoc = latestLocations[faculty.id]
                val room = latestLoc?.roomId?.let { roomMap[it] }
                val building = room?.buildingId?.let { buildingMap[it] }
                val privacy = privacyMap[faculty.id]
                val isSharing = privacy?.locationSharingEnabled ?: true

                val freshness = when {
                    latestLoc == null -> LocationFreshness.NOT_UPDATED
                    now - latestLoc.updatedAt < (45 * 60 * 1000) -> LocationFreshness.CURRENT
                    now - latestLoc.updatedAt < (120 * 60 * 1000) -> LocationFreshness.POSSIBLY_OUTDATED
                    else -> LocationFreshness.LAST_KNOWN
                }

                val facultyTimetables = activities.timetables.filter { it.facultyId == faculty.id }
                    .map { entry ->
                        val entryRoom = roomMap[entry.roomId]
                        val entryBuilding = entryRoom?.buildingId?.let { buildingMap[it] }
                        TimetableWithRoom(entry, entryRoom, entryBuilding)
                    }

                val expectedSlot = facultyTimetables.firstOrNull()

                FacultyDetailedItem(
                    user = faculty,
                    department = faculty.departmentId?.let { deptMap[it] },
                    latestLocation = latestLoc,
                    currentRoom = room,
                    currentBuilding = building,
                    isSharingLocation = isSharing,
                    todayTimetable = facultyTimetables,
                    expectedLocationFromSchedule = expectedSlot,
                    locationFreshness = freshness
                )
            }
        }
    }

    // 2. Authentication
    suspend fun login(email: String, passwordRaw: String): User? = withContext(Dispatchers.IO) {
        val user = userDao.getUserByEmail(email.trim())
        if (user != null && user.isActive && user.passwordHash == passwordRaw.trim()) {
            logDao.insertLog(ActivityLog(userId = user.id, action = "${user.fullName} (${user.role}) logged in."))
            user
        } else {
            null
        }
    }

    suspend fun registerStudent(fullName: String, email: String, passwordRaw: String, deptId: Long): Result<User> = withContext(Dispatchers.IO) {
        val existing = userDao.getUserByEmail(email.trim())
        if (existing != null) {
            return@withContext Result.failure(Exception("An account with this email already exists."))
        }
        val newUser = User(
            fullName = fullName.trim(),
            email = email.trim(),
            passwordHash = passwordRaw.trim(),
            role = UserRole.STUDENT.name,
            departmentId = deptId,
            designation = "Student",
            facultyCode = "STU-${(1000..9999).random()}"
        )
        val id = userDao.insertUser(newUser)
        val created = newUser.copy(id = id)
        logDao.insertLog(ActivityLog(userId = id, action = "Student ${created.fullName} registered."))
        Result.success(created)
    }

    // 3. Faculty Actions
    suspend fun updateFacultyLocation(
        facultyId: Long,
        roomId: Long,
        status: AvailabilityStatus,
        message: String?
    ) = withContext(Dispatchers.IO) {
        val newLoc = FacultyLocation(
            facultyId = facultyId,
            roomId = roomId,
            availabilityStatus = status.name,
            message = message?.trim()?.ifEmpty { null },
            updatedAt = System.currentTimeMillis()
        )
        locationDao.insertLocation(newLoc)
        val faculty = userDao.getUserById(facultyId)
        val room = campusDao.getRoomById(roomId)
        val building = room?.buildingId?.let { campusDao.getBuildingById(it) }
        val locationDesc = if (building != null && room != null) "${building.buildingName}, ${room.floorNumber}, Rm ${room.roomNumber}" else "Room #$roomId"
        logDao.insertLog(
            ActivityLog(
                userId = facultyId,
                action = "${faculty?.fullName ?: "Faculty"} updated location to $locationDesc [Status: ${status.displayName}]."
            )
        )
    }

    suspend fun updateFacultyAvailabilityOnly(
        facultyId: Long,
        status: AvailabilityStatus,
        message: String?
    ) = withContext(Dispatchers.IO) {
        val latest = locationDao.getLocationHistoryForFaculty(facultyId).first().firstOrNull()
        val newLoc = FacultyLocation(
            facultyId = facultyId,
            roomId = latest?.roomId,
            availabilityStatus = status.name,
            message = message?.trim()?.ifEmpty { latest?.message },
            updatedAt = System.currentTimeMillis()
        )
        locationDao.insertLocation(newLoc)
        val faculty = userDao.getUserById(facultyId)
        logDao.insertLog(
            ActivityLog(
                userId = facultyId,
                action = "${faculty?.fullName ?: "Faculty"} updated status to ${status.displayName}."
            )
        )
    }

    suspend fun setFacultyLocationSharing(facultyId: Long, enabled: Boolean) = withContext(Dispatchers.IO) {
        privacyDao.setPrivacy(FacultyPrivacy(facultyId = facultyId, locationSharingEnabled = enabled, updatedAt = System.currentTimeMillis()))
        val faculty = userDao.getUserById(facultyId)
        logDao.insertLog(
            ActivityLog(
                userId = facultyId,
                action = "${faculty?.fullName ?: "Faculty"} ${if (enabled) "enabled" else "paused"} location sharing."
            )
        )
    }

    fun getFacultyPrivacy(facultyId: Long): Flow<FacultyPrivacy?> = privacyDao.getPrivacyForFaculty(facultyId)
    fun getFacultyTimetable(facultyId: Long): Flow<List<TimetableEntry>> = timetableDao.getTimetableForFaculty(facultyId)
    fun getFacultyLocationHistory(facultyId: Long): Flow<List<FacultyLocation>> = locationDao.getLocationHistoryForFaculty(facultyId)

    // 4. Campus Infrastructure Data
    fun getAllDepartments(): Flow<List<Department>> = deptDao.getAllDepartments()
    fun getAllBuildings(): Flow<List<Building>> = campusDao.getAllBuildings()
    fun getAllRooms(): Flow<List<Room>> = campusDao.getAllRooms()

    fun getAllRoomsWithBuildings(): Flow<List<RoomWithBuilding>> {
        return combine(campusDao.getAllRooms(), campusDao.getAllBuildings()) { rooms, buildings ->
            val bMap = buildings.associateBy { it.id }
            rooms.mapNotNull { room ->
                val b = bMap[room.buildingId]
                if (b != null) RoomWithBuilding(room, b) else null
            }
        }
    }

    // 5. Admin Actions
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()
    fun getActivityLogs(): Flow<List<ActivityLog>> = logDao.getRecentActivityLogs()

    suspend fun addFacultyUser(user: User) = withContext(Dispatchers.IO) {
        val id = userDao.insertUser(user)
        privacyDao.setPrivacy(FacultyPrivacy(facultyId = id, locationSharingEnabled = true))
        logDao.insertLog(ActivityLog(userId = id, action = "Created new faculty account: ${user.fullName} (${user.facultyCode})."))
    }

    suspend fun toggleUserActive(userId: Long, isActive: Boolean) = withContext(Dispatchers.IO) {
        userDao.setUserActiveStatus(userId, isActive)
        val user = userDao.getUserById(userId)
        logDao.insertLog(ActivityLog(userId = userId, action = "Account ${user?.fullName} marked ${if (isActive) "ACTIVE" else "DEACTIVATED"}."))
    }

    suspend fun deleteUser(userId: Long) = withContext(Dispatchers.IO) {
        val user = userDao.getUserById(userId)
        userDao.deleteUser(userId)
        logDao.insertLog(ActivityLog(userId = 1, action = "Deleted account: ${user?.fullName}."))
    }

    suspend fun addDepartment(dept: Department) = withContext(Dispatchers.IO) {
        deptDao.insertDepartment(dept)
    }

    suspend fun deleteDepartment(deptId: Long) = withContext(Dispatchers.IO) {
        deptDao.deleteDepartment(deptId)
    }

    suspend fun addBuilding(b: Building) = withContext(Dispatchers.IO) {
        campusDao.insertBuilding(b)
    }

    suspend fun deleteBuilding(bId: Long) = withContext(Dispatchers.IO) {
        campusDao.deleteBuilding(bId)
    }

    suspend fun addRoom(r: Room) = withContext(Dispatchers.IO) {
        campusDao.insertRoom(r)
    }

    suspend fun deleteRoom(rId: Long) = withContext(Dispatchers.IO) {
        campusDao.deleteRoom(rId)
    }

    suspend fun addTimetableEntry(entry: TimetableEntry) = withContext(Dispatchers.IO) {
        timetableDao.insertTimetableEntry(entry)
        val faculty = userDao.getUserById(entry.facultyId)
        logDao.insertLog(ActivityLog(userId = entry.facultyId, action = "Added timetable schedule for ${faculty?.fullName ?: "Faculty"}: ${entry.subjectName} (${entry.dayOfWeek} ${entry.startTime})."))
    }

    suspend fun deleteTimetableEntry(id: Long) = withContext(Dispatchers.IO) {
        timetableDao.deleteTimetableEntry(id)
    }

    // 6. Admin stats computation
    fun getAdminStats(): Flow<AdminDashboardStats> {
        return combine(
            userDao.getAllUsers(),
            locationDao.getAllLocations(),
            deptDao.getAllDepartments(),
            campusDao.getAllBuildings()
        ) { users, locations, depts, buildings ->
            val now = System.currentTimeMillis()
            val facultyUsers = users.filter { it.role == UserRole.FACULTY.name }
            val studentUsers = users.filter { it.role == UserRole.STUDENT.name }

            val latestLocations = locations.groupBy { it.facultyId }.mapValues { (_, locs) ->
                locs.maxByOrNull { it.updatedAt }
            }

            var availableCount = 0
            var inClassCount = 0
            var outdatedCount = 0

            facultyUsers.forEach { f ->
                val loc = latestLocations[f.id]
                if (loc != null) {
                    if (loc.availabilityStatus == AvailabilityStatus.AVAILABLE.name) availableCount++
                    if (loc.availabilityStatus == AvailabilityStatus.IN_CLASS.name) inClassCount++
                    if (now - loc.updatedAt > 45 * 60 * 1000) outdatedCount++
                } else {
                    outdatedCount++
                }
            }

            AdminDashboardStats(
                totalFaculty = facultyUsers.size,
                totalStudents = studentUsers.size,
                availableCount = availableCount,
                inClassCount = inClassCount,
                outdatedLocationCount = outdatedCount,
                totalDepartments = depts.size,
                totalBuildings = buildings.size
            )
        }
    }
}
