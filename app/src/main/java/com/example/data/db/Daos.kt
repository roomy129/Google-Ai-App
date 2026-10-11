package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ActivityLog
import com.example.data.model.Building
import com.example.data.model.Department
import com.example.data.model.FacultyLocation
import com.example.data.model.FacultyPrivacy
import com.example.data.model.Room
import com.example.data.model.TimetableEntry
import com.example.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY fullName ASC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE role = :role AND isActive = 1 ORDER BY fullName ASC")
    fun getUsersByRole(role: String): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): User?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>)

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET isActive = :isActive WHERE id = :id")
    suspend fun setUserActiveStatus(id: Long, isActive: Boolean)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUser(id: Long)
}

@Dao
interface DepartmentDao {
    @Query("SELECT * FROM departments ORDER BY departmentName ASC")
    fun getAllDepartments(): Flow<List<Department>>

    @Query("SELECT * FROM departments WHERE id = :id LIMIT 1")
    suspend fun getDepartmentById(id: Long): Department?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDepartment(department: Department): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDepartments(departments: List<Department>)

    @Query("DELETE FROM departments WHERE id = :id")
    suspend fun deleteDepartment(id: Long)
}

@Dao
interface CampusDao {
    @Query("SELECT * FROM buildings ORDER BY buildingName ASC")
    fun getAllBuildings(): Flow<List<Building>>

    @Query("SELECT * FROM buildings WHERE id = :id LIMIT 1")
    suspend fun getBuildingById(id: Long): Building?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuilding(building: Building): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuildings(buildings: List<Building>)

    @Query("DELETE FROM buildings WHERE id = :id")
    suspend fun deleteBuilding(id: Long)

    @Query("SELECT * FROM rooms ORDER BY buildingId ASC, floorNumber ASC, roomNumber ASC")
    fun getAllRooms(): Flow<List<Room>>

    @Query("SELECT * FROM rooms WHERE buildingId = :buildingId ORDER BY floorNumber ASC, roomNumber ASC")
    fun getRoomsByBuilding(buildingId: Long): Flow<List<Room>>

    @Query("SELECT * FROM rooms WHERE id = :id LIMIT 1")
    suspend fun getRoomById(id: Long): Room?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: Room): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRooms(rooms: List<Room>)

    @Query("DELETE FROM rooms WHERE id = :id")
    suspend fun deleteRoom(id: Long)
}

@Dao
interface FacultyLocationDao {
    @Query("SELECT * FROM faculty_locations ORDER BY updatedAt DESC")
    fun getAllLocations(): Flow<List<FacultyLocation>>

    @Query("SELECT * FROM faculty_locations WHERE facultyId = :facultyId ORDER BY updatedAt DESC LIMIT 1")
    fun getLatestLocationForFaculty(facultyId: Long): Flow<FacultyLocation?>

    @Query("SELECT * FROM faculty_locations WHERE facultyId = :facultyId ORDER BY updatedAt DESC")
    fun getLocationHistoryForFaculty(facultyId: Long): Flow<List<FacultyLocation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: FacultyLocation): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocations(locations: List<FacultyLocation>)
}

@Dao
interface FacultyPrivacyDao {
    @Query("SELECT * FROM faculty_privacy WHERE facultyId = :facultyId LIMIT 1")
    fun getPrivacyForFaculty(facultyId: Long): Flow<FacultyPrivacy?>

    @Query("SELECT * FROM faculty_privacy")
    fun getAllPrivacySettings(): Flow<List<FacultyPrivacy>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setPrivacy(privacy: FacultyPrivacy)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrivacyList(privacies: List<FacultyPrivacy>)
}

@Dao
interface TimetableDao {
    @Query("SELECT * FROM timetable ORDER BY dayOfWeek ASC, startTime ASC")
    fun getAllTimetableEntries(): Flow<List<TimetableEntry>>

    @Query("SELECT * FROM timetable WHERE facultyId = :facultyId ORDER BY dayOfWeek ASC, startTime ASC")
    fun getTimetableForFaculty(facultyId: Long): Flow<List<TimetableEntry>>

    @Query("SELECT * FROM timetable WHERE facultyId = :facultyId AND dayOfWeek = :dayOfWeek ORDER BY startTime ASC")
    fun getTimetableForFacultyDay(facultyId: Long, dayOfWeek: String): Flow<List<TimetableEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetableEntry(entry: TimetableEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetableEntries(entries: List<TimetableEntry>)

    @Query("DELETE FROM timetable WHERE id = :id")
    suspend fun deleteTimetableEntry(id: Long)
}

@Dao
interface ActivityLogDao {
    @Query("SELECT * FROM activity_logs ORDER BY createdAt DESC LIMIT 50")
    fun getRecentActivityLogs(): Flow<List<ActivityLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ActivityLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<ActivityLog>)
}
