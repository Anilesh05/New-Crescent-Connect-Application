with open('app/src/main/java/com/example/data/repository/AttendanceRepositoryImpl.kt', 'r') as f:
    content = f.read()

old_sessionEntity = """fun AttendanceSession.toEntity() = AttendanceSessionEntity(
    id = id,
    courseId = courseId,
    date = date,
    periodNumber = periodNumber,
    markedBy = markedBy,
    markerRole = markerRole.name,
    syncStatus = syncStatus.name,
    timestamp = timestamp
)"""

new_sessionEntity = """fun AttendanceSession.toEntity() = AttendanceSessionEntity(
    id = id,
    courseId = courseId,
    date = date,
    periodNumber = periodNumber,
    markedBy = markedBy,
    markerRole = markerRole.name,
    syncStatus = syncStatus.name,
    timestamp = timestamp,
    isSelfAttendanceEnabled = isSelfAttendanceEnabled,
    startTime = startTime,
    endTime = endTime,
    latitude = latitude,
    longitude = longitude,
    allowedRadiusMeters = allowedRadiusMeters
)"""

old_recordEntity = """fun AttendanceRecord.toEntity() = AttendanceRecordEntity(
    id = id,
    sessionId = sessionId,
    studentId = studentId,
    status = status.name,
    timestamp = timestamp,
    syncStatus = syncStatus.name
)"""

new_recordEntity = """fun AttendanceRecord.toEntity() = AttendanceRecordEntity(
    id = id,
    sessionId = sessionId,
    studentId = studentId,
    status = status.name,
    timestamp = timestamp,
    syncStatus = syncStatus.name,
    verificationMethod = verificationMethod,
    verifiedAt = verifiedAt,
    distanceFromCenter = distanceFromCenter,
    locationAccuracy = locationAccuracy
)"""

content = content.replace(old_sessionEntity, new_sessionEntity)
content = content.replace(old_recordEntity, new_recordEntity)

with open('app/src/main/java/com/example/data/repository/AttendanceRepositoryImpl.kt', 'w') as f:
    f.write(content)
