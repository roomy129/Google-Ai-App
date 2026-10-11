package com.example.data.db

import com.example.data.model.ActivityLog
import com.example.data.model.AvailabilityStatus
import com.example.data.model.Building
import com.example.data.model.Department
import com.example.data.model.FacultyLocation
import com.example.data.model.FacultyPrivacy
import com.example.data.model.Room as RoomEntity
import com.example.data.model.TimetableEntry
import com.example.data.model.User
import com.example.data.model.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseInitializer {

    suspend fun populateInitialData(database: AppDatabase) = withContext(Dispatchers.IO) {
        val userDao = database.userDao()
        val deptDao = database.departmentDao()
        val campusDao = database.campusDao()
        val locationDao = database.facultyLocationDao()
        val privacyDao = database.facultyPrivacyDao()
        val timetableDao = database.timetableDao()
        val logDao = database.activityLogDao()

        // 1. Departments
        val departments = listOf(
            Department(id = 1, departmentName = "Computer Science & Engineering", code = "CSE"),
            Department(id = 2, departmentName = "Electronics & Communication", code = "ECE"),
            Department(id = 3, departmentName = "Information Technology", code = "IT"),
            Department(id = 4, departmentName = "Mechanical Engineering", code = "MECH"),
            Department(id = 5, departmentName = "Mathematics & Computing", code = "MATH")
        )
        deptDao.insertDepartments(departments)

        // 2. Buildings
        val buildings = listOf(
            Building(id = 1, buildingName = "CSE Block (Turing Hall)", campusName = "North Campus", code = "CSE-B"),
            Building(id = 2, buildingName = "Tech Tower (Kalam Complex)", campusName = "Central Campus", code = "TT-A"),
            Building(id = 3, buildingName = "Ramanujan Science Block", campusName = "South Campus", code = "RSB"),
            Building(id = 4, buildingName = "Main Academic Complex", campusName = "Central Campus", code = "MAC")
        )
        campusDao.insertBuildings(buildings)

        // 3. Rooms
        val rooms = listOf(
            // CSE Block
            RoomEntity(id = 1, buildingId = 1, floorNumber = "Second Floor", roomNumber = "204", roomName = "CSE Staff Room"),
            RoomEntity(id = 2, buildingId = 1, floorNumber = "Third Floor", roomNumber = "305", roomName = "AI & ML Research Lab"),
            RoomEntity(id = 3, buildingId = 1, floorNumber = "Second Floor", roomNumber = "201", roomName = "Cloud Computing Lab"),
            RoomEntity(id = 4, buildingId = 1, floorNumber = "First Floor", roomNumber = "102", roomName = "Lecture Hall 102"),
            // Tech Tower
            RoomEntity(id = 5, buildingId = 2, floorNumber = "Ground Floor", roomNumber = "004", roomName = "Advanced Robotics Lab"),
            RoomEntity(id = 6, buildingId = 2, floorNumber = "Second Floor", roomNumber = "210", roomName = "VLSI & Embedded Systems Lab"),
            RoomEntity(id = 7, buildingId = 2, floorNumber = "Fourth Floor", roomNumber = "401", roomName = "Conference Room Alpha"),
            // Ramanujan Science Block
            RoomEntity(id = 8, buildingId = 3, floorNumber = "Third Floor", roomNumber = "302", roomName = "Applied Mathematics Lounge"),
            RoomEntity(id = 9, buildingId = 3, floorNumber = "First Floor", roomNumber = "108", roomName = "Numerical Simulation Room"),
            // Main Academic Complex
            RoomEntity(id = 10, buildingId = 4, floorNumber = "Ground Floor", roomNumber = "101", roomName = "Auditorium & Seminar Hall"),
            RoomEntity(id = 11, buildingId = 4, floorNumber = "Second Floor", roomNumber = "215", roomName = "Mechanical CAD/CAM Lab"),
            RoomEntity(id = 12, buildingId = 4, floorNumber = "Third Floor", roomNumber = "310", roomName = "Dean Academic Affairs Suite")
        )
        campusDao.insertRooms(rooms)

        // 4. Users (Faculty, Students, Admin)
        val users = listOf(
            // Admin
            User(
                id = 1,
                fullName = "Dr. K. Ramanathan",
                email = "admin@campus.edu",
                passwordHash = "admin123",
                role = UserRole.ADMIN.name,
                departmentId = 1,
                designation = "Dean of Academic Affairs",
                facultyCode = "ADM-001"
            ),
            // Student
            User(
                id = 2,
                fullName = "Aarav Mehta",
                email = "student@campus.edu",
                passwordHash = "student123",
                role = UserRole.STUDENT.name,
                departmentId = 1,
                designation = "Student - B.Tech CSE 3rd Year",
                facultyCode = "STU-2024"
            ),
            // Faculty Members (8)
            User(
                id = 3,
                fullName = "Dr. Rajesh Sharma",
                email = "rajesh.sharma@campus.edu",
                passwordHash = "faculty123",
                role = UserRole.FACULTY.name,
                departmentId = 1,
                designation = "Professor & Head of Department",
                facultyCode = "FAC-101"
            ),
            User(
                id = 4,
                fullName = "Dr. Priya Varma",
                email = "priya.varma@campus.edu",
                passwordHash = "faculty123",
                role = UserRole.FACULTY.name,
                departmentId = 1,
                designation = "Associate Professor",
                facultyCode = "FAC-102"
            ),
            User(
                id = 5,
                fullName = "Prof. Amit Patel",
                email = "amit.patel@campus.edu",
                passwordHash = "faculty123",
                role = UserRole.FACULTY.name,
                departmentId = 3,
                designation = "Assistant Professor",
                facultyCode = "FAC-103"
            ),
            User(
                id = 6,
                fullName = "Dr. Ananya Iyer",
                email = "ananya.iyer@campus.edu",
                passwordHash = "faculty123",
                role = UserRole.FACULTY.name,
                departmentId = 2,
                designation = "Professor",
                facultyCode = "FAC-104"
            ),
            User(
                id = 7,
                fullName = "Prof. Vikram Malhotra",
                email = "vikram.malhotra@campus.edu",
                passwordHash = "faculty123",
                role = UserRole.FACULTY.name,
                departmentId = 4,
                designation = "Assistant Professor",
                facultyCode = "FAC-105"
            ),
            User(
                id = 8,
                fullName = "Dr. Sunita Kulkarni",
                email = "sunita.kulkarni@campus.edu",
                passwordHash = "faculty123",
                role = UserRole.FACULTY.name,
                departmentId = 5,
                designation = "Associate Professor",
                facultyCode = "FAC-106"
            ),
            User(
                id = 9,
                fullName = "Prof. Rohan Sen",
                email = "rohan.sen@campus.edu",
                passwordHash = "faculty123",
                role = UserRole.FACULTY.name,
                departmentId = 1,
                designation = "Assistant Professor",
                facultyCode = "FAC-107"
            ),
            User(
                id = 10,
                fullName = "Dr. Neha Reddy",
                email = "neha.reddy@campus.edu",
                passwordHash = "faculty123",
                role = UserRole.FACULTY.name,
                departmentId = 2,
                designation = "Associate Professor",
                facultyCode = "FAC-108"
            )
        )
        userDao.insertUsers(users)

        // Current time references
        val now = System.currentTimeMillis()
        val tenMinsAgo = now - (10 * 60 * 1000)
        val twentyMinsAgo = now - (20 * 60 * 1000)
        val thirtyMinsAgo = now - (30 * 60 * 1000)
        val twoHoursAgo = now - (120 * 60 * 1000) // Outdated demonstrator
        val threeHoursAgo = now - (180 * 60 * 1000)

        // 5. Faculty Locations
        val locations = listOf(
            // Dr. Rajesh Sharma - Available in CSE Staff Room
            FacultyLocation(
                id = 1,
                facultyId = 3,
                roomId = 1, // CSE Staff Room
                availabilityStatus = AvailabilityStatus.AVAILABLE.name,
                message = "Available for student queries & project sign-offs until 2:00 PM.",
                updatedAt = tenMinsAgo
            ),
            // Dr. Priya Varma - In Laboratory (AI & ML Lab)
            FacultyLocation(
                id = 2,
                facultyId = 4,
                roomId = 2, // AI Lab
                availabilityStatus = AvailabilityStatus.IN_LABORATORY.name,
                message = "Mentoring Final Year Deep Learning Capstone projects.",
                updatedAt = twentyMinsAgo
            ),
            // Prof. Amit Patel - In Class (Lecture Hall 102)
            FacultyLocation(
                id = 3,
                facultyId = 5,
                roomId = 4, // LH 102
                availabilityStatus = AvailabilityStatus.IN_CLASS.name,
                message = "Delivering IT402 Cloud Architecture lecture.",
                updatedAt = thirtyMinsAgo
            ),
            // Dr. Ananya Iyer - In Meeting (Conference Room Alpha)
            FacultyLocation(
                id = 4,
                facultyId = 6,
                roomId = 7, // Conference Room Alpha
                availabilityStatus = AvailabilityStatus.IN_MEETING.name,
                message = "Departmental Curriculum Revision Committee meeting.",
                updatedAt = twentyMinsAgo
            ),
            // Prof. Vikram Malhotra - Outdated Location (CAD/CAM Lab, updated 2 hours ago)
            FacultyLocation(
                id = 5,
                facultyId = 7,
                roomId = 11, // CAD Lab
                availabilityStatus = AvailabilityStatus.BUSY.name,
                message = "Calibrating 3D Printer & CNC controllers.",
                updatedAt = twoHoursAgo // Will show as POSSIBLY_OUTDATED
            ),
            // Dr. Sunita Kulkarni - Available in Math Lounge
            FacultyLocation(
                id = 6,
                facultyId = 8,
                roomId = 8, // Applied Math Lounge
                availabilityStatus = AvailabilityStatus.AVAILABLE.name,
                message = "Doubt clearing session for Engineering Mathematics III.",
                updatedAt = tenMinsAgo
            ),
            // Prof. Rohan Sen - Location sharing disabled demonstration
            FacultyLocation(
                id = 7,
                facultyId = 9,
                roomId = 3, // Cloud Lab
                availabilityStatus = AvailabilityStatus.BUSY.name,
                message = "Grading mid-term assignments.",
                updatedAt = thirtyMinsAgo
            ),
            // Dr. Neha Reddy - Robotics Lab
            FacultyLocation(
                id = 8,
                facultyId = 10,
                roomId = 5, // Robotics Lab
                availabilityStatus = AvailabilityStatus.IN_LABORATORY.name,
                message = "Demonstrating Microcontroller interfacing.",
                updatedAt = threeHoursAgo // Outdated
            )
        )
        locationDao.insertLocations(locations)

        // 6. Faculty Privacy Settings
        // Prof. Rohan Sen (facultyId = 9) has location sharing disabled!
        val privacyList = listOf(
            FacultyPrivacy(id = 1, facultyId = 3, locationSharingEnabled = true, updatedAt = now),
            FacultyPrivacy(id = 2, facultyId = 4, locationSharingEnabled = true, updatedAt = now),
            FacultyPrivacy(id = 3, facultyId = 5, locationSharingEnabled = true, updatedAt = now),
            FacultyPrivacy(id = 4, facultyId = 6, locationSharingEnabled = true, updatedAt = now),
            FacultyPrivacy(id = 5, facultyId = 7, locationSharingEnabled = true, updatedAt = now),
            FacultyPrivacy(id = 6, facultyId = 8, locationSharingEnabled = true, updatedAt = now),
            FacultyPrivacy(id = 7, facultyId = 9, locationSharingEnabled = false, updatedAt = now), // Hidden
            FacultyPrivacy(id = 8, facultyId = 10, locationSharingEnabled = true, updatedAt = now)
        )
        privacyDao.insertPrivacyList(privacyList)

        // 7. Timetable Entries
        val timetableEntries = listOf(
            // Dr. Rajesh Sharma
            TimetableEntry(id = 1, facultyId = 3, dayOfWeek = "Monday", startTime = "09:00 AM", endTime = "10:30 AM", subjectName = "CS501 Distributed Systems", roomId = 4),
            TimetableEntry(id = 2, facultyId = 3, dayOfWeek = "Monday", startTime = "11:00 AM", endTime = "12:30 PM", subjectName = "Office Consultation Hours", roomId = 1),
            TimetableEntry(id = 3, facultyId = 3, dayOfWeek = "Monday", startTime = "02:00 PM", endTime = "04:00 PM", subjectName = "CS502 Cloud Computing Lab", roomId = 3),
            TimetableEntry(id = 4, facultyId = 3, dayOfWeek = "Tuesday", startTime = "10:00 AM", endTime = "11:30 AM", subjectName = "CS501 Distributed Systems", roomId = 4),
            TimetableEntry(id = 5, facultyId = 3, dayOfWeek = "Wednesday", startTime = "09:00 AM", endTime = "10:30 AM", subjectName = "CS501 Distributed Systems", roomId = 4),

            // Dr. Priya Varma
            TimetableEntry(id = 6, facultyId = 4, dayOfWeek = "Monday", startTime = "10:00 AM", endTime = "11:30 AM", subjectName = "CS602 Machine Learning", roomId = 4),
            TimetableEntry(id = 7, facultyId = 4, dayOfWeek = "Monday", startTime = "01:30 PM", endTime = "03:30 PM", subjectName = "AI & Deep Learning Lab", roomId = 2),
            TimetableEntry(id = 8, facultyId = 4, dayOfWeek = "Tuesday", startTime = "02:00 PM", endTime = "03:30 PM", subjectName = "CS602 Machine Learning", roomId = 4),

            // Prof. Amit Patel
            TimetableEntry(id = 9, facultyId = 5, dayOfWeek = "Monday", startTime = "09:30 AM", endTime = "11:00 AM", subjectName = "IT402 Cloud Architecture", roomId = 4),
            TimetableEntry(id = 10, facultyId = 5, dayOfWeek = "Monday", startTime = "02:00 PM", endTime = "04:00 PM", subjectName = "Web Technologies Lab", roomId = 3),

            // Dr. Ananya Iyer
            TimetableEntry(id = 11, facultyId = 6, dayOfWeek = "Monday", startTime = "11:00 AM", endTime = "12:30 PM", subjectName = "EC304 Digital Signal Processing", roomId = 7),
            TimetableEntry(id = 12, facultyId = 6, dayOfWeek = "Tuesday", startTime = "09:00 AM", endTime = "11:00 AM", subjectName = "VLSI Systems Lab", roomId = 6),

            // Prof. Vikram Malhotra
            TimetableEntry(id = 13, facultyId = 7, dayOfWeek = "Monday", startTime = "08:30 AM", endTime = "10:30 AM", subjectName = "ME203 Thermodynamics", roomId = 11),
            TimetableEntry(id = 14, facultyId = 7, dayOfWeek = "Monday", startTime = "11:00 AM", endTime = "01:00 PM", subjectName = "CAD/CAM Modeling Lab", roomId = 11),

            // Dr. Sunita Kulkarni
            TimetableEntry(id = 15, facultyId = 8, dayOfWeek = "Monday", startTime = "10:00 AM", endTime = "11:30 AM", subjectName = "MA201 Engineering Mathematics III", roomId = 9),
            TimetableEntry(id = 16, facultyId = 8, dayOfWeek = "Monday", startTime = "02:00 PM", endTime = "03:30 PM", subjectName = "Discrete Mathematical Structures", roomId = 9),

            // Prof. Rohan Sen
            TimetableEntry(id = 17, facultyId = 9, dayOfWeek = "Monday", startTime = "09:00 AM", endTime = "11:00 AM", subjectName = "CS201 Data Structures & Algorithms", roomId = 4),

            // Dr. Neha Reddy
            TimetableEntry(id = 18, facultyId = 10, dayOfWeek = "Monday", startTime = "11:30 AM", endTime = "01:00 PM", subjectName = "EE401 Embedded Controllers", roomId = 5)
        )
        timetableDao.insertTimetableEntries(timetableEntries)

        // 8. Activity Logs
        val sampleLogs = listOf(
            ActivityLog(id = 1, userId = 3, action = "Dr. Rajesh Sharma updated location to CSE Block - Second Floor, Rm 204 (CSE Staff Room). Status: Available", createdAt = tenMinsAgo),
            ActivityLog(id = 2, userId = 4, action = "Dr. Priya Varma updated location to CSE Block - Third Floor, Rm 305 (AI & ML Research Lab). Status: In Laboratory", createdAt = twentyMinsAgo),
            ActivityLog(id = 3, userId = 5, action = "Prof. Amit Patel updated location to CSE Block - First Floor, Rm 102. Status: In Class", createdAt = thirtyMinsAgo),
            ActivityLog(id = 4, userId = 9, action = "Prof. Rohan Sen disabled public location sharing.", createdAt = thirtyMinsAgo),
            ActivityLog(id = 5, userId = 1, action = "Administrator Dr. K. Ramanathan added new timetable slot for CS501 Distributed Systems.", createdAt = now - (60 * 60 * 1000))
        )
        logDao.insertLogs(sampleLogs)
    }
}
