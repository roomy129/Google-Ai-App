import datetime
from models import db, User, Department, Building, Room, FacultyLocation, FacultyPrivacy, Timetable, ActivityLog

def seed_database():
    if User.query.first():
        return

    # 1. Departments
    depts = [
        Department(id=1, department_name="Computer Science & Engineering", code="CSE"),
        Department(id=2, department_name="Electronics & Communication", code="ECE"),
        Department(id=3, department_name="Information Technology", code="IT"),
        Department(id=4, department_name="Mechanical Engineering", code="MECH"),
        Department(id=5, department_name="Mathematics & Computing", code="MATH")
    ]
    db.session.bulk_save_objects(depts)
    db.session.commit()

    # 2. Buildings
    buildings = [
        Building(id=1, building_name="CSE Block (Turing Hall)", campus_name="North Campus", code="CSE-B"),
        Building(id=2, building_name="Tech Tower (Kalam Complex)", campus_name="Central Campus", code="TT-A"),
        Building(id=3, building_name="Ramanujan Science Block", campus_name="South Campus", code="RSB"),
        Building(id=4, building_name="Main Academic Complex", campus_name="Central Campus", code="MAC")
    ]
    db.session.bulk_save_objects(buildings)
    db.session.commit()

    # 3. Rooms
    rooms = [
        Room(id=1, building_id=1, floor_number="Second Floor", room_number="204", room_name="CSE Staff Room"),
        Room(id=2, building_id=1, floor_number="Third Floor", room_number="305", room_name="AI & ML Research Lab"),
        Room(id=3, building_id=1, floor_number="Second Floor", room_number="201", room_name="Cloud Computing Lab"),
        Room(id=4, building_id=1, floor_number="First Floor", room_number="102", room_name="Lecture Hall 102"),
        Room(id=5, building_id=2, floor_number="Ground Floor", room_number="004", room_name="Advanced Robotics Lab"),
        Room(id=6, building_id=2, floor_number="Second Floor", room_number="210", room_name="VLSI Design Lab"),
        Room(id=7, building_id=2, floor_number="Fourth Floor", room_number="401", room_name="Conference Room Alpha"),
        Room(id=8, building_id=3, floor_number="Third Floor", room_number="302", room_name="Applied Mathematics Lounge"),
        Room(id=9, building_id=3, floor_number="First Floor", room_number="108", room_name="Numerical Simulation Room"),
        Room(id=10, building_id=4, floor_number="Ground Floor", room_number="101", room_name="Auditorium & Seminar Hall"),
        Room(id=11, building_id=4, floor_number="Second Floor", room_number="215", room_name="Mechanical CAD/CAM Lab"),
        Room(id=12, building_id=4, floor_number="Third Floor", room_number="310", room_name="Dean Academic Affairs Suite")
    ]
    db.session.bulk_save_objects(rooms)
    db.session.commit()

    # 4. Users (Admin, Student, 8 Faculty)
    admin = User(id=1, full_name="Dr. K. Ramanathan", email="admin@campus.edu", role="ADMIN", department_id=1, designation="Dean of Academic Affairs", faculty_code="ADM-001")
    admin.set_password("admin123")

    student = User(id=2, full_name="Aarav Mehta", email="student@campus.edu", role="STUDENT", department_id=1, designation="Student - B.Tech CSE", faculty_code="STU-2024")
    student.set_password("student123")

    faculty_list = [
        User(id=3, full_name="Dr. Rajesh Sharma", email="rajesh.sharma@campus.edu", role="FACULTY", department_id=1, designation="Professor & HOD", faculty_code="FAC-101"),
        User(id=4, full_name="Dr. Priya Varma", email="priya.varma@campus.edu", role="FACULTY", department_id=1, designation="Associate Professor", faculty_code="FAC-102"),
        User(id=5, full_name="Prof. Amit Patel", email="amit.patel@campus.edu", role="FACULTY", department_id=3, designation="Assistant Professor", faculty_code="FAC-103"),
        User(id=6, full_name="Dr. Ananya Iyer", email="ananya.iyer@campus.edu", role="FACULTY", department_id=2, designation="Professor", faculty_code="FAC-104"),
        User(id=7, full_name="Prof. Vikram Malhotra", email="vikram.malhotra@campus.edu", role="FACULTY", department_id=4, designation="Assistant Professor", faculty_code="FAC-105"),
        User(id=8, full_name="Dr. Sunita Kulkarni", email="sunita.kulkarni@campus.edu", role="FACULTY", department_id=5, designation="Associate Professor", faculty_code="FAC-106"),
        User(id=9, full_name="Prof. Rohan Sen", email="rohan.sen@campus.edu", role="FACULTY", department_id=1, designation="Assistant Professor", faculty_code="FAC-107"),
        User(id=10, full_name="Dr. Neha Reddy", email="neha.reddy@campus.edu", role="FACULTY", department_id=2, designation="Associate Professor", faculty_code="FAC-108")
    ]
    for f in faculty_list:
        f.set_password("faculty123")

    db.session.add(admin)
    db.session.add(student)
    for f in faculty_list:
        db.session.add(f)
    db.session.commit()

    now = datetime.datetime.utcnow()
    # 5. Locations
    locations = [
        FacultyLocation(faculty_id=3, room_id=1, availability_status="AVAILABLE", message="Available for queries until 2:00 PM.", updated_at=now - datetime.timedelta(minutes=10)),
        FacultyLocation(faculty_id=4, room_id=2, availability_status="IN_LABORATORY", message="Mentoring Deep Learning Capstone projects.", updated_at=now - datetime.timedelta(minutes=20)),
        FacultyLocation(faculty_id=5, room_id=4, availability_status="IN_CLASS", message="Delivering IT402 lecture.", updated_at=now - datetime.timedelta(minutes=30)),
        FacultyLocation(faculty_id=6, room_id=7, availability_status="IN_MEETING", message="Curriculum Committee meeting.", updated_at=now - datetime.timedelta(minutes=20)),
        FacultyLocation(faculty_id=7, room_id=11, availability_status="BUSY", message="Calibrating 3D Printer.", updated_at=now - datetime.timedelta(hours=2)),
        FacultyLocation(faculty_id=8, room_id=8, availability_status="AVAILABLE", message="Doubt clearing session.", updated_at=now - datetime.timedelta(minutes=15)),
        FacultyLocation(faculty_id=9, room_id=3, availability_status="BUSY", message="Grading midterm papers.", updated_at=now - datetime.timedelta(minutes=30)),
        FacultyLocation(faculty_id=10, room_id=5, availability_status="IN_LABORATORY", message="Lab experiment session.", updated_at=now - datetime.timedelta(hours=3))
    ]
    for loc in locations:
        db.session.add(loc)

    # 6. Privacy
    privacies = [
        FacultyPrivacy(faculty_id=3, location_sharing_enabled=True),
        FacultyPrivacy(faculty_id=4, location_sharing_enabled=True),
        FacultyPrivacy(faculty_id=5, location_sharing_enabled=True),
        FacultyPrivacy(faculty_id=6, location_sharing_enabled=True),
        FacultyPrivacy(faculty_id=7, location_sharing_enabled=True),
        FacultyPrivacy(faculty_id=8, location_sharing_enabled=True),
        FacultyPrivacy(faculty_id=9, location_sharing_enabled=False), # Hidden
        FacultyPrivacy(faculty_id=10, location_sharing_enabled=True)
    ]
    for p in privacies:
        db.session.add(p)

    # 7. Timetable
    timetables = [
        Timetable(faculty_id=3, day_of_week="Monday", start_time="09:00 AM", end_time="10:30 AM", subject_name="CS501 Distributed Systems", room_id=4),
        Timetable(faculty_id=3, day_of_week="Monday", start_time="11:00 AM", end_time="12:30 PM", subject_name="Consultation Hours", room_id=1),
        Timetable(faculty_id=4, day_of_week="Monday", start_time="10:00 AM", end_time="11:30 AM", subject_name="CS602 Machine Learning", room_id=4),
        Timetable(faculty_id=5, day_of_week="Monday", start_time="09:30 AM", end_time="11:00 AM", subject_name="IT402 Cloud Architecture", room_id=4),
        Timetable(faculty_id=6, day_of_week="Monday", start_time="11:00 AM", end_time="12:30 PM", subject_name="EC304 DSP", room_id=7),
        Timetable(faculty_id=7, day_of_week="Monday", start_time="08:30 AM", end_time="10:30 AM", subject_name="ME203 Thermodynamics", room_id=11)
    ]
    for tt in timetables:
        db.session.add(tt)

    db.session.commit()
