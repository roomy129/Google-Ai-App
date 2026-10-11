package com.example.data.model

data class RoomWithBuilding(
    val room: Room,
    val building: Building
) {
    val fullDisplayName: String
        get() = "${building.buildingName} - ${room.floorNumber}, Rm ${room.roomNumber} (${room.roomName})"
}

data class TimetableWithRoom(
    val entry: TimetableEntry,
    val room: Room?,
    val building: Building?
) {
    val locationDisplay: String
        get() = if (building != null && room != null) {
            "${building.buildingName}, ${room.floorNumber}, Room ${room.roomNumber}"
        } else {
            "Room details unavailable"
        }
}

data class FacultyDetailedItem(
    val user: User,
    val department: Department?,
    val latestLocation: FacultyLocation?,
    val currentRoom: Room?,
    val currentBuilding: Building?,
    val isSharingLocation: Boolean,
    val todayTimetable: List<TimetableWithRoom>,
    val expectedLocationFromSchedule: TimetableWithRoom?,
    val locationFreshness: LocationFreshness
) {
    val displayName: String get() = user.fullName
    val facultyCode: String get() = user.facultyCode ?: "FAC-${user.id}"
    val departmentName: String get() = department?.departmentName ?: "General Academics"
    val designation: String get() = user.designation ?: "Assistant Professor"

    val availabilityStatus: AvailabilityStatus
        get() = if (latestLocation != null) {
            AvailabilityStatus.fromString(latestLocation.availabilityStatus)
        } else {
            AvailabilityStatus.AVAILABLE
        }

    val displayLocationText: String
        get() = when {
            !isSharingLocation -> "Location sharing paused by faculty member"
            latestLocation == null || currentRoom == null || currentBuilding == null -> "Location not updated yet"
            else -> "${currentBuilding.buildingName}, ${currentRoom.floorNumber}, Rm ${currentRoom.roomNumber} (${currentRoom.roomName})"
        }

    val buildingName: String?
        get() = if (isSharingLocation) currentBuilding?.buildingName else null

    val roomInfo: String?
        get() = if (isSharingLocation && currentRoom != null) "${currentRoom.floorNumber}, Rm ${currentRoom.roomNumber}" else null

    val statusMessage: String?
        get() = if (isSharingLocation) latestLocation?.message else null

    val lastUpdatedTimestamp: Long?
        get() = latestLocation?.updatedAt
}

data class AdminDashboardStats(
    val totalFaculty: Int = 0,
    val totalStudents: Int = 0,
    val availableCount: Int = 0,
    val inClassCount: Int = 0,
    val outdatedLocationCount: Int = 0,
    val totalDepartments: Int = 0,
    val totalBuildings: Int = 0
)
