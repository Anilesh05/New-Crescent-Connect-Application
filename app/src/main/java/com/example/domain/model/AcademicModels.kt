package com.example.domain.model

data class Conversation(
    val id: String,
    val type: String,
    val courseId: String?,
    val participant1Id: String?,
    val participant2Id: String?
)

data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderRole: String,
    val messageText: String,
    val timestamp: Long,
    val attachmentUri: String?,
    val isRead: Boolean,
    val syncStatus: SyncStatus = SyncStatus.SYNCED
)

data class Announcement(
    val id: String,
    val staffId: String,
    val courseId: String,
    val section: String,
    val title: String,
    val message: String,
    val attachmentUri: String?,
    val createdAt: Long,
    val isImportant: Boolean,
    val syncStatus: SyncStatus = SyncStatus.SYNCED
)

data class StudyMaterial(
    val id: String,
    val courseId: String,
    val staffId: String,
    val title: String,
    val description: String,
    val fileName: String,
    val fileType: String,
    val storagePath: String?,
    val createdAt: Long,
    val syncStatus: SyncStatus = SyncStatus.SYNCED
)

data class Assignment(
    val id: String,
    val courseId: String,
    val staffId: String,
    val title: String,
    val description: String,
    val dueDate: Long,
    val attachmentUri: String?,
    val createdAt: Long,
    val syncStatus: SyncStatus = SyncStatus.SYNCED
)

data class AssignmentCompletion(
    val id: String,
    val assignmentId: String,
    val studentId: String,
    val completedAt: Long,
    val syncStatus: SyncStatus = SyncStatus.SYNCED
)

data class Notification(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String,
    val timestamp: Long,
    val isRead: Boolean
)
