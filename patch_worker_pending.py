with open('app/src/main/java/com/example/data/sync/AttendanceSyncWorker.kt', 'r') as f:
    content = f.read()

sync_records = """            val pendingRecords = attendanceDao.getPendingRecords()
            for (record in pendingRecords) {
                try {
                    val recordRef = firestore.collection("attendanceSessions")
                        .document(record.sessionId)
                        .collection("records")
                        .document(record.studentId)
                        
                    val recordMap = hashMapOf(
                        "studentId" to record.studentId,
                        "status" to record.status,
                        "markedAt" to record.timestamp,
                        "updatedAt" to System.currentTimeMillis(),
                        "verificationMethod" to record.verificationMethod,
                        "verifiedAt" to record.verifiedAt,
                        "distanceFromCenter" to record.distanceFromCenter,
                        "locationAccuracy" to record.locationAccuracy
                    )
                    recordRef.set(recordMap).await()
                    attendanceDao.updateRecordSyncStatus(record.id, SyncStatus.SYNCED.name)
                } catch (e: Exception) {
                    Log.e("SyncWorker", "Failed to sync independent record ${record.id}", e)
                }
            }

            Result.success()"""

content = content.replace("            Result.success()", sync_records)

with open('app/src/main/java/com/example/data/sync/AttendanceSyncWorker.kt', 'w') as f:
    f.write(content)
