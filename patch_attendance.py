with open('app/src/main/java/com/example/data/local/entity/AttendanceEntities.kt', 'r') as f:
    content = f.read()

new_session = """@Entity(tableName = "attendance_sessions")
data class AttendanceSessionEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val date: String,
    val periodNumber: Int,
    val markedBy: String,
    val markerRole: String,
    val syncStatus: String,
    val timestamp: Long,
    val isSelfAttendanceEnabled: Boolean = false,
    val startTime: Long? = null,
    val endTime: Long? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val allowedRadiusMeters: Int? = null
)"""

new_record = """@Entity(tableName = "attendance_records")
data class AttendanceRecordEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val studentId: String,
    val status: String,
    val timestamp: Long,
    val syncStatus: String,
    val verificationMethod: String = "MANUAL",
    val verifiedAt: Long? = null,
    val distanceFromCenter: Float? = null,
    val locationAccuracy: Float? = null
)"""

content = content.replace(
    '@Entity(tableName = "attendance_sessions")\ndata class AttendanceSessionEntity(\n    @PrimaryKey val id: String,\n    val courseId: String,\n    val date: String,\n    val periodNumber: Int,\n    val markedBy: String,\n    val markerRole: String,\n    val syncStatus: String,\n    val timestamp: Long\n)',
    new_session
)

content = content.replace(
    '@Entity(tableName = "attendance_records")\ndata class AttendanceRecordEntity(\n    @PrimaryKey val id: String,\n    val sessionId: String,\n    val studentId: String,\n    val status: String,\n    val timestamp: Long,\n    val syncStatus: String\n)',
    new_record
)

with open('app/src/main/java/com/example/data/local/entity/AttendanceEntities.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/domain/model/Attendance.kt', 'r') as f:
    content2 = f.read()

new_session2 = """data class AttendanceSession(
    val id: String,
    val courseId: String,
    val date: String,
    val periodNumber: Int,
    val markedBy: String,
    val markerRole: Role,
    val syncStatus: SyncStatus,
    val timestamp: Long,
    val isSelfAttendanceEnabled: Boolean = false,
    val startTime: Long? = null,
    val endTime: Long? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val allowedRadiusMeters: Int? = null
)"""

new_record2 = """data class AttendanceRecord(
    val id: String,
    val sessionId: String,
    val studentId: String,
    val status: AttendanceStatus,
    val timestamp: Long,
    val syncStatus: SyncStatus,
    val verificationMethod: String = "MANUAL",
    val verifiedAt: Long? = null,
    val distanceFromCenter: Float? = null,
    val locationAccuracy: Float? = null
)"""

content2 = content2.replace(
    'data class AttendanceSession(\n    val id: String,\n    val courseId: String,\n    val date: String,\n    val periodNumber: Int,\n    val markedBy: String,\n    val markerRole: Role,\n    val syncStatus: SyncStatus,\n    val timestamp: Long\n)',
    new_session2
)

content2 = content2.replace(
    'data class AttendanceRecord(\n    val id: String,\n    val sessionId: String,\n    val studentId: String,\n    val status: AttendanceStatus,\n    val timestamp: Long,\n    val syncStatus: SyncStatus\n)',
    new_record2
)

with open('app/src/main/java/com/example/domain/model/Attendance.kt', 'w') as f:
    f.write(content2)

