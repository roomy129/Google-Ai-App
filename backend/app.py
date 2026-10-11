import os
import datetime
from flask import Flask, jsonify, request
from flask_cors import CORS
from models import db, User, Department, Building, Room, FacultyLocation, FacultyPrivacy, Timetable, ActivityLog
from database import seed_database

app = Flask(__name__)
app.config['SECRET_KEY'] = os.getenv('SECRET_KEY', 'campus-smart-tracker-secret-2026')
app.config['SQLALCHEMY_DATABASE_URI'] = os.getenv('DATABASE_URL', 'sqlite:///campus_tracking.db')
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

CORS(app)
db.init_app(app)

with app.app_context():
    db.create_all()
    seed_database()

# ----------------- AUTHENTICATION ROUTES -----------------
@app.route('/api/auth/login', methods=['POST'])
def login():
    data = request.get_json() or {}
    email = data.get('email', '').strip()
    password = data.get('password', '').strip()

    user = User.query.filter_by(email=email).first()
    if not user or not user.check_password(password) or not user.is_active:
        return jsonify({'error': 'Invalid credentials or inactive account'}), 401

    log = ActivityLog(user_id=user.id, action=f"{user.full_name} logged in ({user.role}).")
    db.session.add(log)
    db.session.commit()

    return jsonify({
        'message': 'Login successful',
        'user': user.to_dict()
    }), 200

@app.route('/api/auth/register', methods=['POST'])
def register():
    data = request.get_json() or {}
    full_name = data.get('full_name', '').strip()
    email = data.get('email', '').strip()
    password = data.get('password', '').strip()
    department_id = data.get('department_id')

    if not full_name or not email or not password:
        return jsonify({'error': 'Full name, email, and password are required'}), 400

    if User.query.filter_by(email=email).first():
        return jsonify({'error': 'Email already registered'}), 400

    new_user = User(
        full_name=full_name,
        email=email,
        role='STUDENT',
        department_id=department_id,
        designation='Student',
        faculty_code=f"STU-{(hash(email) % 9000) + 1000}"
    )
    new_user.set_password(password)
    db.session.add(new_user)
    db.session.commit()

    return jsonify({'message': 'Registration successful', 'user': new_user.to_dict()}), 201

@app.route('/api/auth/me', methods=['GET'])
def get_me():
    user_id = request.args.get('user_id')
    if not user_id:
        return jsonify({'error': 'Missing user_id parameter'}), 400
    user = User.query.get(user_id)
    if not user:
        return jsonify({'error': 'User not found'}), 404
    return jsonify(user.to_dict()), 200

# ----------------- FACULTY DIRECTORY & LOCATION ROUTES -----------------
@app.route('/api/faculty', methods=['GET'])
def get_faculty_list():
    faculty_users = User.query.filter_by(role='FACULTY', is_active=True).all()
    result = []
    now = datetime.datetime.utcnow()

    for f in faculty_users:
        latest_loc = FacultyLocation.query.filter_by(faculty_id=f.id).order_by(FacultyLocation.updated_at.desc()).first()
        privacy = FacultyPrivacy.query.filter_by(faculty_id=f.id).first()
        is_sharing = privacy.location_sharing_enabled if privacy else True

        # Freshness calculation
        freshness = "NOT_UPDATED"
        if latest_loc:
            diff_mins = (now - latest_loc.updated_at).total_seconds() / 60.0
            if diff_mins < 45:
                freshness = "CURRENT"
            elif diff_mins < 120:
                freshness = "POSSIBLY_OUTDATED"
            else:
                freshness = "LAST_KNOWN"

        location_info = None
        if is_sharing and latest_loc:
            location_info = latest_loc.to_dict()

        result.append({
            'user': f.to_dict(),
            'is_sharing_location': is_sharing,
            'location_freshness': freshness,
            'latest_location': location_info,
            'availability_status': latest_loc.availability_status if latest_loc else 'AVAILABLE',
            'status_message': latest_loc.message if (is_sharing and latest_loc) else None
        })

    return jsonify(result), 200

@app.route('/api/faculty/<int:faculty_id>', methods=['GET'])
def get_faculty_details(faculty_id):
    faculty = User.query.filter_by(id=faculty_id, role='FACULTY').first()
    if not faculty:
        return jsonify({'error': 'Faculty member not found'}), 404

    latest_loc = FacultyLocation.query.filter_by(faculty_id=faculty.id).order_by(FacultyLocation.updated_at.desc()).first()
    privacy = FacultyPrivacy.query.filter_by(faculty_id=faculty.id).first()
    is_sharing = privacy.location_sharing_enabled if privacy else True
    timetable = Timetable.query.filter_by(faculty_id=faculty.id).all()

    return jsonify({
        'user': faculty.to_dict(),
        'is_sharing_location': is_sharing,
        'latest_location': latest_loc.to_dict() if (is_sharing and latest_loc) else None,
        'availability_status': latest_loc.availability_status if latest_loc else 'AVAILABLE',
        'timetable': [t.to_dict() for t in timetable]
    }), 200

@app.route('/api/faculty/location', methods=['PUT'])
def update_location():
    data = request.get_json() or {}
    faculty_id = data.get('faculty_id')
    room_id = data.get('room_id')
    status = data.get('availability_status', 'AVAILABLE')
    message = data.get('message')

    new_loc = FacultyLocation(
        faculty_id=faculty_id,
        room_id=room_id,
        availability_status=status,
        message=message,
        updated_at=datetime.datetime.utcnow()
    )
    db.session.add(new_loc)
    log = ActivityLog(user_id=faculty_id, action=f"Faculty updated location to Room #{room_id} [{status}].")
    db.session.add(log)
    db.session.commit()

    return jsonify({'message': 'Location updated successfully', 'location': new_loc.to_dict()}), 200

@app.route('/api/faculty/privacy', methods=['PUT'])
def update_privacy():
    data = request.get_json() or {}
    faculty_id = data.get('faculty_id')
    enabled = data.get('location_sharing_enabled', True)

    priv = FacultyPrivacy.query.filter_by(faculty_id=faculty_id).first()
    if not priv:
        priv = FacultyPrivacy(faculty_id=faculty_id, location_sharing_enabled=enabled)
        db.session.add(priv)
    else:
        priv.location_sharing_enabled = enabled
        priv.updated_at = datetime.datetime.utcnow()

    db.session.commit()
    return jsonify({'message': 'Privacy settings updated', 'privacy': priv.to_dict()}), 200

# ----------------- ADMIN ROUTES -----------------
@app.route('/api/admin/dashboard', methods=['GET'])
def get_admin_dashboard():
    total_faculty = User.query.filter_by(role='FACULTY').count()
    total_students = User.query.filter_by(role='STUDENT').count()
    total_depts = Department.query.count()
    total_buildings = Building.query.count()

    return jsonify({
        'total_faculty': total_faculty,
        'total_students': total_students,
        'total_departments': total_depts,
        'total_buildings': total_buildings
    }), 200

@app.route('/api/admin/departments', methods=['GET', 'POST'])
def manage_departments():
    if request.method == 'POST':
        data = request.get_json() or {}
        dept = Department(department_name=data['department_name'], code=data['code'].upper())
        db.session.add(dept)
        db.session.commit()
        return jsonify(dept.to_dict()), 201
    depts = Department.query.all()
    return jsonify([d.to_dict() for d in depts]), 200

@app.route('/api/admin/buildings', methods=['GET', 'POST'])
def manage_buildings():
    if request.method == 'POST':
        data = request.get_json() or {}
        b = Building(building_name=data['building_name'], campus_name=data.get('campus_name', 'Main Campus'), code=data['code'].upper())
        db.session.add(b)
        db.session.commit()
        return jsonify(b.to_dict()), 201
    buildings = Building.query.all()
    return jsonify([b.to_dict() for b in buildings]), 200

@app.route('/api/admin/rooms', methods=['GET', 'POST'])
def manage_rooms():
    if request.method == 'POST':
        data = request.get_json() or {}
        r = Room(building_id=data['building_id'], floor_number=data['floor_number'], room_number=data['room_number'], room_name=data['room_name'])
        db.session.add(r)
        db.session.commit()
        return jsonify(r.to_dict()), 201
    rooms = Room.query.all()
    return jsonify([r.to_dict() for r in rooms]), 200

if __name__ == '__main__':
    port = int(os.environ.get('PORT', 5000))
    app.run(host='0.0.0.0', port=port, debug=True)
