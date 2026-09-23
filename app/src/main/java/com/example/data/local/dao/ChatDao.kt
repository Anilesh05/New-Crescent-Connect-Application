package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM conversations WHERE courseId = :courseId LIMIT 1")
    suspend fun getConversationByCourseId(courseId: String): ConversationEntity?
    
    @Query("SELECT * FROM conversations WHERE type = 'DIRECT' AND ((participant1Id = :p1 AND participant2Id = :p2) OR (participant1Id = :p2 AND participant2Id = :p1)) LIMIT 1")
    suspend fun getDirectConversation(p1: String, p2: String): ConversationEntity?

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: String): Flow<List<MessageEntity>>
    
    @Query("SELECT * FROM messages WHERE syncStatus = 'PENDING'")
    suspend fun getPendingMessages(): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<ConversationEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)
    
    @Query("UPDATE messages SET syncStatus = :status WHERE id = :id")
    suspend fun updateMessageSyncStatus(id: String, status: String)
}
