package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.ActivityLog
import com.example.data.model.AdminDashboardStats
import com.example.data.model.AvailabilityStatus
import com.example.data.model.Building
import com.example.data.model.Department
import com.example.data.model.FacultyDetailedItem
import com.example.data.model.FacultyLocation
import com.example.data.model.FacultyPrivacy
import com.example.data.model.Room
import com.example.data.model.RoomWithBuilding
import com.example.data.model.TimetableEntry
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.repository.FacultyTrackingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenDestination {
    LANDING,
    LOGIN,
    STUDENT_DASHBOARD,
    FACULTY_DASHBOARD,
    ADMIN_DASHBOARD
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    val repository = FacultyTrackingRepository(database)

    // Current Session State
    private val _currentScreen = MutableStateFlow(ScreenDestination.STUDENT_DASHBOARD)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Notification banner / snackbar
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Student Filter States
    val searchQuery = MutableStateFlow("")
    val selectedDepartmentId = MutableStateFlow<Long?>(null)
    val selectedAvailability = MutableStateFlow<AvailabilityStatus?>(null)

    // Selected Faculty for detailed profile view
    private val _selectedFacultyDetail = MutableStateFlow<FacultyDetailedItem?>(null)
    val selectedFacultyDetail: StateFlow<FacultyDetailedItem?> = _selectedFacultyDetail.asStateFlow()

    // Base streams
    val allFaculty: StateFlow<List<FacultyDetailedItem>> = repository.getAllFacultyDetailed()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val departments: StateFlow<List<Department>> = repository.getAllDepartments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val buildings: StateFlow<List<Building>> = repository.getAllBuildings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val roomsWithBuildings: StateFlow<List<RoomWithBuilding>> = repository.getAllRoomsWithBuildings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminStats: StateFlow<AdminDashboardStats> = repository.getAdminStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminDashboardStats())

    val allUsers: StateFlow<List<User>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activityLogs: StateFlow<List<ActivityLog>> = repository.getActivityLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Faculty for Student Dashboard
    val filteredFaculty: StateFlow<List<FacultyDetailedItem>> = combine(
        allFaculty,
        searchQuery,
        selectedDepartmentId,
        selectedAvailability
    ) { facultyList, query, deptId, status ->
        facultyList.filter { item ->
            val matchesQuery = query.isBlank() ||
                item.displayName.contains(query, ignoreCase = true) ||
                item.facultyCode.contains(query, ignoreCase = true) ||
                item.departmentName.contains(query, ignoreCase = true) ||
                (item.buildingName?.contains(query, ignoreCase = true) == true) ||
                (item.roomInfo?.contains(query, ignoreCase = true) == true)

            val matchesDept = deptId == null || item.user.departmentId == deptId
            val matchesStatus = status == null || item.availabilityStatus == status

            matchesQuery && matchesDept && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Faculty personal state (for logged in faculty)
    private val _facultyLocationHistory = MutableStateFlow<List<FacultyLocation>>(emptyList())
    val facultyLocationHistory: StateFlow<List<FacultyLocation>> = _facultyLocationHistory.asStateFlow()

    private val _facultyPrivacy = MutableStateFlow<FacultyPrivacy?>(null)
    val facultyPrivacy: StateFlow<FacultyPrivacy?> = _facultyPrivacy.asStateFlow()

    init {
        viewModelScope.launch {
            repository.checkAndInitializeDatabase()
            // Set default demo session as student for instant preview
            loginWithDemoRole(UserRole.STUDENT)
        }
    }

    fun navigateTo(screen: ScreenDestination) {
        _currentScreen.value = screen
    }

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun selectFacultyDetail(faculty: FacultyDetailedItem?) {
        _selectedFacultyDetail.value = faculty
    }

    // Role switching & authentication
    fun loginWithDemoRole(role: UserRole) {
        viewModelScope.launch {
            val users = repository.getAllUsers()
            // Pull first user with matching role
            val targetEmail = when (role) {
                UserRole.STUDENT -> "student@campus.edu"
                UserRole.FACULTY -> "rajesh.sharma@campus.edu"
                UserRole.ADMIN -> "admin@campus.edu"
            }
            val user = repository.login(targetEmail, when (role) {
                UserRole.STUDENT -> "student123"
                UserRole.FACULTY -> "faculty123"
                UserRole.ADMIN -> "admin123"
            })
            if (user != null) {
                _currentUser.value = user
                when (role) {
                    UserRole.STUDENT -> navigateTo(ScreenDestination.STUDENT_DASHBOARD)
                    UserRole.FACULTY -> {
                        loadFacultySpecificData(user.id)
                        navigateTo(ScreenDestination.FACULTY_DASHBOARD)
                    }
                    UserRole.ADMIN -> navigateTo(ScreenDestination.ADMIN_DASHBOARD)
                }
                showToast("Logged in as ${user.fullName} (${user.role})")
            }
        }
    }

    fun login(email: String, passwordRaw: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val user = repository.login(email, passwordRaw)
            if (user != null) {
                _currentUser.value = user
                when (UserRole.valueOf(user.role)) {
                    UserRole.STUDENT -> navigateTo(ScreenDestination.STUDENT_DASHBOARD)
                    UserRole.FACULTY -> {
                        loadFacultySpecificData(user.id)
                        navigateTo(ScreenDestination.FACULTY_DASHBOARD)
                    }
                    UserRole.ADMIN -> navigateTo(ScreenDestination.ADMIN_DASHBOARD)
                }
                showToast("Welcome back, ${user.fullName}!")
                onResult(true, "Success")
            } else {
                onResult(false, "Invalid credentials or account inactive.")
            }
        }
    }

    fun registerStudent(fullName: String, email: String, passwordRaw: String, deptId: Long, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = repository.registerStudent(fullName, email, passwordRaw, deptId)
            res.onSuccess { user ->
                _currentUser.value = user
                navigateTo(ScreenDestination.STUDENT_DASHBOARD)
                showToast("Welcome to Faculty Finder, ${user.fullName}!")
                onResult(true, "Registration successful")
            }.onFailure { err ->
                onResult(false, err.message ?: "Registration failed")
            }
        }
    }

    fun logout() {
        val user = _currentUser.value
        _currentUser.value = null
        navigateTo(ScreenDestination.LANDING)
        showToast("Logged out successfully.")
    }

    private fun loadFacultySpecificData(facultyId: Long) {
        viewModelScope.launch {
            repository.getFacultyLocationHistory(facultyId).collect {
                _facultyLocationHistory.value = it
            }
        }
        viewModelScope.launch {
            repository.getFacultyPrivacy(facultyId).collect {
                _facultyPrivacy.value = it
            }
        }
    }

    // Faculty Actions
    fun updateLocation(roomId: Long, status: AvailabilityStatus, message: String?) {
        val faculty = _currentUser.value ?: return
        if (faculty.role != UserRole.FACULTY.name) return

        viewModelScope.launch {
            repository.updateFacultyLocation(faculty.id, roomId, status, message)
            showToast("Location updated successfully! Live on campus radar.")
        }
    }

    fun updateAvailabilityOnly(status: AvailabilityStatus, message: String?) {
        val faculty = _currentUser.value ?: return
        if (faculty.role != UserRole.FACULTY.name) return

        viewModelScope.launch {
            repository.updateFacultyAvailabilityOnly(faculty.id, status, message)
            showToast("Availability status updated to ${status.displayName}.")
        }
    }

    fun toggleLocationSharing(enabled: Boolean) {
        val faculty = _currentUser.value ?: return
        if (faculty.role != UserRole.FACULTY.name) return

        viewModelScope.launch {
            repository.setFacultyLocationSharing(faculty.id, enabled)
            _facultyPrivacy.value = _facultyPrivacy.value?.copy(locationSharingEnabled = enabled)
                ?: FacultyPrivacy(facultyId = faculty.id, locationSharingEnabled = enabled)
            showToast(if (enabled) "Location sharing enabled for students." else "Location sharing paused. Details are hidden from students.")
        }
    }

    // Admin Actions
    fun addFaculty(name: String, email: String, deptId: Long, designation: String, code: String) {
        viewModelScope.launch {
            val newFaculty = User(
                fullName = name.trim(),
                email = email.trim(),
                passwordHash = "faculty123",
                role = UserRole.FACULTY.name,
                departmentId = deptId,
                designation = designation.trim(),
                facultyCode = code.trim().uppercase()
            )
            repository.addFacultyUser(newFaculty)
            showToast("Faculty account created: $name ($code)")
        }
    }

    fun toggleUserStatus(user: User) {
        viewModelScope.launch {
            repository.toggleUserActive(user.id, !user.isActive)
            showToast("Updated account status for ${user.fullName}.")
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            repository.deleteUser(user.id)
            showToast("Removed account ${user.fullName}.")
        }
    }

    fun addDepartment(name: String, code: String) {
        viewModelScope.launch {
            repository.addDepartment(Department(departmentName = name.trim(), code = code.trim().uppercase()))
            showToast("Department '$name' added.")
        }
    }

    fun addBuilding(name: String, campus: String, code: String) {
        viewModelScope.launch {
            repository.addBuilding(Building(buildingName = name.trim(), campusName = campus.trim(), code = code.trim().uppercase()))
            showToast("Building '$name' added.")
        }
    }

    fun addRoom(buildingId: Long, floor: String, number: String, name: String) {
        viewModelScope.launch {
            repository.addRoom(Room(buildingId = buildingId, floorNumber = floor.trim(), roomNumber = number.trim(), roomName = name.trim()))
            showToast("Room '$number ($name)' added.")
        }
    }

    fun addTimetableEntry(facultyId: Long, day: String, start: String, end: String, subject: String, roomId: Long) {
        viewModelScope.launch {
            val entry = TimetableEntry(
                facultyId = facultyId,
                dayOfWeek = day,
                startTime = start.trim(),
                endTime = end.trim(),
                subjectName = subject.trim(),
                roomId = roomId
            )
            repository.addTimetableEntry(entry)
            showToast("Timetable schedule added for $subject.")
        }
    }

    fun deleteTimetableEntry(id: Long) {
        viewModelScope.launch {
            repository.deleteTimetableEntry(id)
            showToast("Timetable entry removed.")
        }
    }

    fun deleteRoom(roomId: Long) {
        viewModelScope.launch {
            repository.deleteRoom(roomId)
            showToast("Room removed from campus.")
        }
    }
}
