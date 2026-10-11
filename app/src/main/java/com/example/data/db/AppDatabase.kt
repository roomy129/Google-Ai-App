package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ActivityLog
import com.example.data.model.Building
import com.example.data.model.Department
import com.example.data.model.FacultyLocation
import com.example.data.model.FacultyPrivacy
import com.example.data.model.Room as RoomEntity
import com.example.data.model.TimetableEntry
import com.example.data.model.User

@Database(
    entities = [
        Department::class,
        Building::class,
        RoomEntity::class,
        User::class,
        FacultyLocation::class,
        FacultyPrivacy::class,
        TimetableEntry::class,
        ActivityLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun departmentDao(): DepartmentDao
    abstract fun campusDao(): CampusDao
    abstract fun facultyLocationDao(): FacultyLocationDao
    abstract fun facultyPrivacyDao(): FacultyPrivacyDao
    abstract fun timetableDao(): TimetableDao
    abstract fun activityLogDao(): ActivityLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "faculty_location_tracking.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
