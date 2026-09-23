package com.example.data.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.local.CrescentDatabase
import com.example.data.local.entity.AttendanceSessionEntity
import com.example.data.local.entity.AttendanceRecordEntity
import com.example.domain.model.SyncStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AttendanceSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val db = CrescentDatabase.getDatabase(applicationContext)
            val attendanceDao = db.attendanceDao()
            
            val firestore = try {
                FirebaseFirestore.getInstance()
            } catch (e: IllegalStateException) {
                Log.i("SyncWorker", "Firebase not initialized. Offline mode only.")
                return Result.success()
            }

            // Note: If you don't have getPendingSessions() in DAO yet, we will need to add it.
            val pendingSessions = attendanceDao.getPendingSessions()
            
            for (session in pendingSessions) {
                try {
                    val sessionRef = firestore.collection("attendanceSessions").document(session.id)
                    val sessionMap = hashMapOf(
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
                    )
                    
                    sessionRef.set(sessionMap).await()

                    val records = attendanceDao.getRecordsForSessionList(session.id)
                    for (record in records) {
                        val recordRef = sessionRef.collection("records").document(record.studentId)
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
                    }

                    attendanceDao.updateSessionSyncStatus(session.id, SyncStatus.SYNCED.name)
                } catch (e: Exception) {
                    Log.e("SyncWorker", "Failed to sync session ${session.id}", e)
                    return Result.retry()
                }
            }

            val pendingRecords = attendanceDao.getPendingRecords()
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

            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error in sync worker", e)
            Result.retry()
        }
    }
}
