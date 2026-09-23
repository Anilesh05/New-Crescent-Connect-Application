package com.example.data.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.local.CrescentDatabase
import com.example.domain.model.SyncStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AcademicSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            val db = CrescentDatabase.getDatabase(applicationContext)
            val academicDao = db.academicDao()
            
            val firestore = try {
                FirebaseFirestore.getInstance()
            } catch (e: IllegalStateException) {
                Log.i("AcademicSyncWorker", "Firebase not initialized. Offline mode only.")
                return Result.success()
            }

            val pendingAnnouncements = academicDao.getPendingAnnouncements()
            for (ann in pendingAnnouncements) {
                try {
                    val annMap = hashMapOf(
                        "id" to ann.id,
                        "staffId" to ann.staffId,
                        "courseId" to ann.courseId,
                        "section" to ann.section,
                        "title" to ann.title,
                        "message" to ann.message,
                        "attachmentUri" to ann.attachmentUri,
                        "createdAt" to ann.createdAt,
                        "isImportant" to ann.isImportant
                    )
                    
                    firestore.collection("announcements").document(ann.id).set(annMap).await()
                    academicDao.updateAnnouncementSyncStatus(ann.id, SyncStatus.SYNCED.name)
                } catch (e: Exception) {
                    Log.e("AcademicSyncWorker", "Failed to sync announcement ${ann.id}", e)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("AcademicSyncWorker", "Error in sync worker", e)
            Result.retry()
        }
    }
}
