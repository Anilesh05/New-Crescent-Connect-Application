import re

with open('app/src/main/java/com/example/data/local/entity/Entities.kt', 'r') as f:
    content = f.read()

# Replace UserEntity
new_user = """@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val passwordHash: String,
    val role: String,
    val registerNumber: String?,
    val programme: String?,
    val department: String?,
    val semester: String?,
    val section: String?,
    val designation: String?,
    val academicYear: String?,
    val profileImageUri: String?,
    val isActive: Boolean,
    val skills: String? = null,
    val certifications: String? = null,
    val projects: String? = null,
    val internships: String? = null,
    val achievements: String? = null,
    val syncStatus: String = "SYNCED"
)"""
content = re.sub(r'@Entity\(tableName = "users"\).*?isActive: Boolean\n\)', new_user, content, flags=re.DOTALL)

# Replace ConversationEntity
new_conv = """@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val type: String = "COURSE",
    val courseId: String? = null,
    val participant1Id: String? = null,
    val participant2Id: String? = null
)"""
content = re.sub(r'@Entity\(tableName = "conversations"\).*?courseId: String\n\)', new_conv, content, flags=re.DOTALL)

# Replace MessageEntity
new_msg = """@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderId: String,
    val senderRole: String = "STUDENT",
    val messageText: String,
    val timestamp: Long,
    val attachmentUri: String?,
    val isRead: Boolean,
    val syncStatus: String = "SYNCED"
)"""
content = re.sub(r'@Entity\(tableName = "messages"\).*?isRead: Boolean\n\)', new_msg, content, flags=re.DOTALL)

# Add new entities
new_entities = """

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey val id: String,
    val staffId: String,
    val courseId: String,
    val section: String,
    val title: String,
    val message: String,
    val attachmentUri: String?,
    val createdAt: Long,
    val isImportant: Boolean,
    val syncStatus: String = "SYNCED"
)

@Entity(tableName = "study_materials")
data class StudyMaterialEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val staffId: String,
    val title: String,
    val description: String,
    val fileName: String,
    val fileType: String,
    val storagePath: String?,
    val createdAt: Long,
    val syncStatus: String = "SYNCED"
)

@Entity(tableName = "assignments")
data class AssignmentEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val staffId: String,
    val title: String,
    val description: String,
    val dueDate: Long,
    val attachmentUri: String?,
    val createdAt: Long,
    val syncStatus: String = "SYNCED"
)

@Entity(tableName = "assignment_completions")
data class AssignmentCompletionEntity(
    @PrimaryKey val id: String,
    val assignmentId: String,
    val studentId: String,
    val completedAt: Long,
    val syncStatus: String = "SYNCED"
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String,
    val timestamp: Long,
    val isRead: Boolean = false
)
"""

with open('app/src/main/java/com/example/data/local/entity/Entities.kt', 'w') as f:
    f.write(content + new_entities)

