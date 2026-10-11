package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "departments",
    indices = [Index(value = ["code"], unique = true)]
)
data class Department(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val departmentName: String,
    val code: String
)

@Entity(tableName = "buildings")
data class Building(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val buildingName: String,
    val campusName: String = "Main Campus",
    val code: String
)

@Entity(
    tableName = "rooms",
    foreignKeys = [
        ForeignKey(
            entity = Building::class,
            parentColumns = ["id"],
            childColumns = ["buildingId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["buildingId"])]
)
data class Room(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val buildingId: Long,
    val floorNumber: String,
    val roomNumber: String,
    val roomName: String
)

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val role: String, // STUDENT, FACULTY, ADMIN
    val departmentId: Long? = null,
    val designation: String? = null,
    val facultyCode: String? = null, // e.g. "FAC-101"
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "faculty_locations",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["facultyId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Room::class,
            parentColumns = ["id"],
            childColumns = ["roomId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["facultyId"]), Index(value = ["roomId"])]
)
data class FacultyLocation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val facultyId: Long,
    val roomId: Long?,
    val availabilityStatus: String, // AvailabilityStatus.name
    val message: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "faculty_privacy",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["facultyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["facultyId"], unique = true)]
)
data class FacultyPrivacy(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val facultyId: Long,
    val locationSharingEnabled: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "timetable",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["facultyId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Room::class,
            parentColumns = ["id"],
            childColumns = ["roomId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["facultyId"]), Index(value = ["roomId"])]
)
data class TimetableEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val facultyId: Long,
    val dayOfWeek: String, // Monday, Tuesday, etc.
    val startTime: String, // e.g. "09:00 AM" or "09:00"
    val endTime: String,   // e.g. "10:30 AM" or "10:30"
    val subjectName: String,
    val roomId: Long
)

@Entity(
    tableName = "activity_logs",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class ActivityLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val action: String,
    val createdAt: Long = System.currentTimeMillis()
)
