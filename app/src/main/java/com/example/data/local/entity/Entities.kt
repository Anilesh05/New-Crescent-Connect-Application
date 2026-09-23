package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
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
    val status: String = "ACTIVE",
    val idVersion: Int = 1,
    val updatedAt: Long? = null,
    val skills: String? = null,
    val certifications: String? = null,
    val projects: String? = null,
    val internships: String? = null,
    val achievements: String? = null,
    val syncStatus: String = "SYNCED"
)

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: String,
    val subjectCode: String,
    val subjectName: String,
    val facultyId: String,
    val programme: String,
    val semester: String,
    val section: String,
    val academicYear: String
)

@Entity(tableName = "enrollments", primaryKeys = ["studentId", "courseId"])
data class EnrollmentEntity(
    val studentId: String,
    val courseId: String
)

@Entity(tableName = "timetables")
data class TimetableEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val dayOfWeek: String,
    val startTime: String,
    val endTime: String,
    val room: String,
    val periodNumber: Int,
    val section: String
)

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val type: String = "COURSE",
    val courseId: String? = null,
    val participant1Id: String? = null,
    val participant2Id: String? = null
)

@Entity(tableName = "messages")
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
)


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
