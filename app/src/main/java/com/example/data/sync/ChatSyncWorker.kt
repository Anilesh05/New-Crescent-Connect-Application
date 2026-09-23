package com.example.data.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.local.CrescentDatabase
import com.example.domain.model.SyncStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ChatSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            val db = CrescentDatabase.getDatabase(applicationContext)
            val chatDao = db.chatDao()
            
            val firestore = try {
                FirebaseFirestore.getInstance()
            } catch (e: IllegalStateException) {
                Log.i("ChatSyncWorker", "Firebase not initialized. Offline mode only.")
                return Result.success()
            }

            val pendingMessages = chatDao.getPendingMessages()
            for (msg in pendingMessages) {
                try {
                    val msgMap = hashMapOf(
                        "id" to msg.id,
                        "conversationId" to msg.conversationId,
                        "senderId" to msg.senderId,
                        "senderRole" to msg.senderRole,
                        "messageText" to msg.messageText,
                        "timestamp" to msg.timestamp,
                        "attachmentUri" to msg.attachmentUri,
                        "isRead" to msg.isRead
                    )
                    
                    firestore.collection("conversations")
                        .document(msg.conversationId)
                        .collection("messages")
                        .document(msg.id)
                        .set(msgMap).await()
                        
                    chatDao.updateMessageSyncStatus(msg.id, SyncStatus.SYNCED.name)
                } catch (e: Exception) {
                    Log.e("ChatSyncWorker", "Failed to sync message ${msg.id}", e)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("ChatSyncWorker", "Error in sync worker", e)
            Result.retry()
        }
    }
}
