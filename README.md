# Faculty Location Tracking System
### *A Smart Campus Solution for Real-Time Faculty Availability and Location*

> **Tagline:** *"Find the Right Faculty, at the Right Place, at the Right Time."*

---

## 1. Project Overview
The **Faculty Location Tracking System** is a smart campus solution designed to eliminate wasted time searching across lecture halls, laboratory complexes, staff rooms, and departmental blocks. 

- **For Students:** Instant search by name, department, or faculty ID; live campus room/floor radar; timetable schedule integration; and clear freshness indicators (Current, Possibly Outdated, Last Known).
- **For Faculty:** Fast, privacy-respecting manual check-ins (Building, Floor, Room, Custom Status Note); availability status updates (Available, In Class, In Lab, In Meeting, Busy, Not Available); and a one-tap privacy toggle to pause location sharing.
- **For Administrators:** Comprehensive campus management dashboard with real-time summary statistics, faculty and student account management, campus infrastructure configuration (Departments, Buildings, Floors, and Rooms), timetable management, and audit activity logs.

---

## 2. Key Architecture & Technology Stack

### A. Android Client (Live in Streaming Emulator)
- **UI Framework:** Kotlin + Jetpack Compose with Material 3 Design
- **Theme:** Collegiate Navy Blue (`#0F294A`), Campus Accent Blue (`#0284C7`), Crisp White surfaces
- **Local Persistence:** Room SQLite Database with reactive Kotlin Coroutine `Flow`
- **State Management:** MVVM Architecture with `MainViewModel` and `StateFlow`
- **Privacy Model:** Completely manual check-ins without GPS tracking or intrusive background geolocation.

### B. Python Flask REST Backend (`/backend/`)
- **Framework:** Python Flask with Flask-CORS
- **ORM / Database:** SQLAlchemy with SQLite (`campus_tracking.db`)
- **Authentication:** Role-based authentication with Werkzeug password hashing
- **Endpoints:** RESTful API for auth, faculty locations, timetables, and administration.

---

## 3. Demo User Accounts

The application is pre-populated with realistic academic data (8 faculty members, 4 campus blocks, 12 rooms, timetables, and audit history). You can log in using either the **⚡ Instant 1-Tap Demo Switcher** in the app header or with the credentials below:

| Role | Name | Email | Password | Details |
|---|---|---|---|---|
| **Student** | Aarav Mehta | `student@campus.edu` | `student123` | B.Tech CSE 3rd Year (`STU-2024`) |
| **Faculty (HOD)** | Dr. Rajesh Sharma | `rajesh.sharma@campus.edu` | `faculty123` | Professor & HOD CSE (`FAC-101`) |
| **Faculty (AI/ML)** | Dr. Priya Varma | `priya.varma@campus.edu` | `faculty123` | Associate Professor (`FAC-102`) |
| **Faculty (Private)** | Prof. Rohan Sen | `rohan.sen@campus.edu` | `faculty123` | Location sharing paused demo (`FAC-107`) |
| **Administrator** | Dr. K. Ramanathan | `admin@campus.edu` | `admin123` | Dean of Academic Affairs (`ADM-001`) |

---

## 4. Location Staleness & Privacy Rules

1. **Current Location:** Updated within the last 45 minutes (`< 45 mins`).
2. **Possibly Outdated:** Updated between 45 minutes and 2 hours ago.
3. **Last Known Location:** Saved earlier today or previous shift (`> 2 hours ago`).
4. **Scheduled vs. Actual:** The system displays both the faculty member's latest manual check-in location and their scheduled timetable location side-by-side without assuming either is incorrect.
5. **Privacy Safeguard:** When a faculty member disables location sharing, their room coordinates are masked from students with the message: *"Location sharing paused by faculty member"*.

---

## 5. Running the Python Flask Backend (Optional Standalone)

To run the Flask REST API independently:
```bash
cd backend
pip install -r requirements.txt
python app.py
```
The server will initialize the SQLite database (`campus_tracking.db`), automatically seed demo records, and serve endpoints on `http://localhost:5000`.

### Core API Endpoints:
- `POST /api/auth/login` - Authenticate user & return role profile
- `POST /api/auth/register` - Register new student account
- `GET /api/faculty` - List all faculty with availability, location, and freshness
- `GET /api/faculty/<id>` - Full faculty profile including timetable
- `PUT /api/faculty/location` - Update faculty room location and status
- `PUT /api/faculty/privacy` - Toggle location sharing
- `GET /api/admin/dashboard` - Admin metrics & campus overview
- `GET/POST /api/admin/departments` - Manage academic departments
- `GET/POST /api/admin/buildings` - Manage campus blocks
- `GET/POST /api/admin/rooms` - Manage rooms and labs

---

## 6. Verification and Testing Scenarios

1. **Search & Filter:** Search for "Sharma" or filter by "CSE" and "Available".
2. **Profile & Campus Map:** Tap "View Details" to see the weekly schedule, comparison between check-in and expected timetable location, and the interactive campus building block visualizer.
3. **Faculty Check-In:** Switch to Faculty role via the top bar, choose building and room (e.g. *Tech Tower - Robotics Lab*), and tap "Update Location & Broadcast". Switch back to Student to observe the real-time update.
4. **Privacy Test:** Toggle "Location Sharing Privacy" to OFF in Faculty Dashboard; return to Student view to confirm room coordinates are hidden.
5. **Administration:** In Admin Dashboard, inspect metrics, create new faculty accounts, add rooms, and review the live activity audit log.
