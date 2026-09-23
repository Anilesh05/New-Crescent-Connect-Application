with open('app/src/main/java/com/example/data/sync/AttendanceSyncWorker.kt', 'r') as f:
    content = f.read()

session_map_old = """                    val sessionMap = hashMapOf(
                        "courseId" to session.courseId,
                        "date" to session.date,
                        "periodNumber" to session.periodNumber,
                        "staffId" to session.markedBy,
                        "markerRole" to session.markerRole,
                        "createdAt" to session.timestamp,
                        "updatedAt" to System.currentTimeMillis()
                    )"""

session_map_new = """                    val sessionMap = hashMapOf(
                        "courseId" to session.courseId,
                        "date" to session.date,
                        "periodNumber" to session.periodNumber,
                        "staffId" to session.markedBy,
                        "markerRole" to session.markerRole,
                        "createdAt" to session.timestamp,
                        "updatedAt" to System.currentTimeMillis(),
                        "isSelfAttendanceEnabled" to session.isSelfAttendanceEnabled,
                        "startTime" to session.startTime,
                        "endTime" to session.endTime,
                        "latitude" to session.latitude,
                        "longitude" to session.longitude,
                        "allowedRadiusMeters" to session.allowedRadiusMeters
                    )"""

record_map_old = """                        val recordMap = hashMapOf(
                            "studentId" to record.studentId,
                            "status" to record.status,
                            "markedAt" to record.timestamp,
                            "updatedAt" to System.currentTimeMillis()
                        )"""

record_map_new = """                        val recordMap = hashMapOf(
                            "studentId" to record.studentId,
                            "status" to record.status,
                            "markedAt" to record.timestamp,
                            "updatedAt" to System.currentTimeMillis(),
                            "verificationMethod" to record.verificationMethod,
                            "verifiedAt" to record.verifiedAt,
                            "distanceFromCenter" to record.distanceFromCenter,
                            "locationAccuracy" to record.locationAccuracy
                        )"""

content = content.replace(session_map_old, session_map_new)
content = content.replace(record_map_old, record_map_new)

with open('app/src/main/java/com/example/data/sync/AttendanceSyncWorker.kt', 'w') as f:
    f.write(content)
