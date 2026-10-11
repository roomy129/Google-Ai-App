import datetime
from flask_sqlalchemy import SQLAlchemy
from werkzeug.security import generate_password_hash, check_password_hash

db = SQLAlchemy()

class Department(db.Model):
    __tablename__ = 'departments'
    id = db.Column(db.Integer, primary_key=True)
    department_name = db.Column(db.String(100), nullable=False)
    code = db.Column(db.String(20), unique=True, nullable=False)

    users = db.relationship('User', backref='department', lazy=True)

    def to_dict(self):
        return {
            'id': self.id,
            'department_name': self.department_name,
            'code': self.code
        }

class Building(db.Model):
    __tablename__ = 'buildings'
    id = db.Column(db.Integer, primary_key=True)
    building_name = db.Column(db.String(100), nullable=False)
    campus_name = db.Column(db.String(100), default='Main Campus')
    code = db.Column(db.String(20), unique=True, nullable=False)

    rooms = db.relationship('Room', backref='building', lazy=True, cascade='all, delete-orphan')

    def to_dict(self):
        return {
            'id': self.id,
            'building_name': self.building_name,
            'campus_name': self.campus_name,
            'code': self.code
        }

class Room(db.Model):
    __tablename__ = 'rooms'
    id = db.Column(db.Integer, primary_key=True)
    building_id = db.Column(db.Integer, db.ForeignKey('buildings.id'), nullable=False)
    floor_number = db.Column(db.String(50), nullable=False)
    room_number = db.Column(db.String(20), nullable=False)
    room_name = db.Column(db.String(100), nullable=False)

    def to_dict(self):
        return {
            'id': self.id,
            'building_id': self.building_id,
            'building_name': self.building.building_name if self.building else None,
            'floor_number': self.floor_number,
            'room_number': self.room_number,
            'room_name': self.room_name
        }

class User(db.Model):
    __tablename__ = 'users'
    id = db.Column(db.Integer, primary_key=True)
    full_name = db.Column(db.String(120), nullable=False)
    email = db.Column(db.String(120), unique=True, nullable=False)
    password_hash = db.Column(db.String(255), nullable=False)
    role = db.Column(db.String(20), nullable=False)  # STUDENT, FACULTY, ADMIN
    department_id = db.Column(db.Integer, db.ForeignKey('departments.id'), nullable=True)
    designation = db.Column(db.String(100), nullable=True)
    faculty_code = db.Column(db.String(30), nullable=True)
    is_active = db.Column(db.Boolean, default=True)
    created_at = db.Column(db.DateTime, default=datetime.datetime.utcnow)

    locations = db.relationship('FacultyLocation', backref='faculty', lazy=True, cascade='all, delete-orphan')
    privacy = db.relationship('FacultyPrivacy', backref='faculty', uselist=False, cascade='all, delete-orphan')
    timetables = db.relationship('Timetable', backref='faculty', lazy=True, cascade='all, delete-orphan')

    def set_password(self, password):
        self.password_hash = generate_password_hash(password)

    def check_password(self, password):
        return check_password_hash(self.password_hash, password)

    def to_dict(self):
        return {
            'id': self.id,
            'full_name': self.full_name,
            'email': self.email,
            'role': self.role,
            'department_id': self.department_id,
            'department_name': self.department.department_name if self.department else None,
            'designation': self.designation,
            'faculty_code': self.faculty_code,
            'is_active': self.is_active,
            'created_at': self.created_at.isoformat()
        }

class FacultyLocation(db.Model):
    __tablename__ = 'faculty_locations'
    id = db.Column(db.Integer, primary_key=True)
    faculty_id = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False)
    room_id = db.Column(db.Integer, db.ForeignKey('rooms.id'), nullable=True)
    availability_status = db.Column(db.String(30), nullable=False)
    message = db.Column(db.String(255), nullable=True)
    updated_at = db.Column(db.DateTime, default=datetime.datetime.utcnow)

    room = db.relationship('Room', backref='locations')

    def to_dict(self):
        return {
            'id': self.id,
            'faculty_id': self.faculty_id,
            'room_id': self.room_id,
            'room_name': self.room.room_name if self.room else None,
            'room_number': self.room.room_number if self.room else None,
            'floor_number': self.room.floor_number if self.room else None,
            'building_name': self.room.building.building_name if self.room and self.room.building else None,
            'availability_status': self.availability_status,
            'message': self.message,
            'updated_at': self.updated_at.isoformat()
        }

class FacultyPrivacy(db.Model):
    __tablename__ = 'faculty_privacy'
    id = db.Column(db.Integer, primary_key=True)
    faculty_id = db.Column(db.Integer, db.ForeignKey('users.id'), unique=True, nullable=False)
    location_sharing_enabled = db.Column(db.Boolean, default=True)
    updated_at = db.Column(db.DateTime, default=datetime.datetime.utcnow)

    def to_dict(self):
        return {
            'faculty_id': self.faculty_id,
            'location_sharing_enabled': self.location_sharing_enabled,
            'updated_at': self.updated_at.isoformat()
        }

class Timetable(db.Model):
    __tablename__ = 'timetable'
    id = db.Column(db.Integer, primary_key=True)
    faculty_id = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False)
    day_of_week = db.Column(db.String(20), nullable=False)
    start_time = db.Column(db.String(20), nullable=False)
    end_time = db.Column(db.String(20), nullable=False)
    subject_name = db.Column(db.String(100), nullable=False)
    room_id = db.Column(db.Integer, db.ForeignKey('rooms.id'), nullable=False)

    room = db.relationship('Room', backref='timetable_entries')

    def to_dict(self):
        return {
            'id': self.id,
            'faculty_id': self.faculty_id,
            'day_of_week': self.day_of_week,
            'start_time': self.start_time,
            'end_time': self.end_time,
            'subject_name': self.subject_name,
            'room_id': self.room_id,
            'room_number': self.room.room_number if self.room else None,
            'building_name': self.room.building.building_name if self.room and self.room.building else None
        }

class ActivityLog(db.Model):
    __tablename__ = 'activity_logs'
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False)
    action = db.Column(db.String(255), nullable=False)
    created_at = db.Column(db.DateTime, default=datetime.datetime.utcnow)

    def to_dict(self):
        return {
            'id': self.id,
            'user_id': self.user_id,
            'action': self.action,
            'created_at': self.created_at.isoformat()
        }
