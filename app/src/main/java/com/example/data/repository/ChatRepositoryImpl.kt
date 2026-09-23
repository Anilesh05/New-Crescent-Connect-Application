package com.example.data.repository

import com.example.data.local.dao.ChatDao
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.MessageEntity
import com.example.domain.model.Conversation
import com.example.domain.model.Message
import com.example.domain.model.SyncStatus
import com.example.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

fun ConversationEntity.toDomain() = Conversation(id, type, courseId, participant1Id, participant2Id)
fun Conversation.toEntity() = ConversationEntity(id, type, courseId, participant1Id, participant2Id)

fun MessageEntity.toDomain() = Message(id, conversationId, senderId, senderRole, messageText, timestamp, attachmentUri, isRead, SyncStatus.valueOf(syncStatus))
fun Message.toEntity() = MessageEntity(id, conversationId, senderId, senderRole, messageText, timestamp, attachmentUri, isRead, syncStatus.name)

class ChatRepositoryImpl(private val dao: ChatDao, private val context: android.content.Context) : ChatRepository {
    override suspend fun getCourseConversation(courseId: String): Conversation {
        var conv = dao.getConversationByCourseId(courseId)
        if (conv == null) {
            val newId = UUID.randomUUID().toString()
            conv = ConversationEntity(newId, "COURSE", courseId, null, null)
            dao.insertConversation(conv)
        }
        return conv.toDomain()
    }

    override suspend fun getDirectConversation(participant1Id: String, participant2Id: String): Conversation {
        var conv = dao.getDirectConversation(participant1Id, participant2Id)
        if (conv == null) {
            val newId = UUID.randomUUID().toString()
            conv = ConversationEntity(newId, "DIRECT", null, participant1Id, participant2Id)
            dao.insertConversation(conv)
        }
        return conv.toDomain()
    }

    override fun getMessages(conversationId: String): Flow<List<Message>> {
        return dao.getMessagesForConversation(conversationId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun sendMessage(message: Message) {
        dao.insertMessage(message.toEntity())

        val constraints = androidx.work.Constraints.Builder().setRequiredNetworkType(androidx.work.NetworkType.CONNECTED).build()
        val syncRequest = androidx.work.OneTimeWorkRequestBuilder<com.example.data.sync.ChatSyncWorker>().setConstraints(constraints).build()
        androidx.work.WorkManager.getInstance(context).enqueue(syncRequest)
    }
}
