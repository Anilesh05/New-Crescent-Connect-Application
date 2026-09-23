package com.example.data.repository

import com.example.data.local.dao.AcademicDao
import com.example.data.local.entity.*
import com.example.domain.model.*
import com.example.domain.repository.AcademicRepository
import com.example.domain.repository.FileStorageService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

fun AnnouncementEntity.toDomain() = Announcement(
    id = id,
    staffId = staffId,
    courseId = courseId,
    section = section,
    title = title,
    message = message,
    attachmentUri = attachmentUri,
    createdAt = createdAt,
    isImportant = isImportant,
    syncStatus = SyncStatus.valueOf(syncStatus)
)
fun Announcement.toEntity() = AnnouncementEntity(id, staffId, courseId, section, title, message, attachmentUri, createdAt, isImportant, syncStatus.name)

fun StudyMaterialEntity.toDomain() = StudyMaterial(id, courseId, staffId, title, description, fileName, fileType, storagePath, createdAt, SyncStatus.valueOf(syncStatus))
fun StudyMaterial.toEntity() = StudyMaterialEntity(id, courseId, staffId, title, description, fileName, fileType, storagePath, createdAt, syncStatus.name)

fun AssignmentEntity.toDomain() = Assignment(id, courseId, staffId, title, description, dueDate, attachmentUri, createdAt, SyncStatus.valueOf(syncStatus))
fun Assignment.toEntity() = AssignmentEntity(id, courseId, staffId, title, description, dueDate, attachmentUri, createdAt, syncStatus.name)

fun AssignmentCompletionEntity.toDomain() = AssignmentCompletion(id, assignmentId, studentId, completedAt, SyncStatus.valueOf(syncStatus))
fun AssignmentCompletion.toEntity() = AssignmentCompletionEntity(id, assignmentId, studentId, completedAt, syncStatus.name)

fun NotificationEntity.toDomain() = Notification(id, userId, title, message, type, timestamp, isRead)
fun Notification.toEntity() = NotificationEntity(id, userId, title, message, type, timestamp, isRead)

class AcademicRepositoryImpl(private val dao: AcademicDao, private val context: android.content.Context, private val fileStorageService: FileStorageService? = null) : AcademicRepository {
    override fun getAnnouncementsForCourse(courseId: String): Flow<List<Announcement>> =
        dao.getAnnouncementsForCourse(courseId).map { list -> list.map { it.toDomain() } }

    override suspend fun createAnnouncement(announcement: Announcement) {
        dao.insertAnnouncement(announcement.toEntity())

        val constraints = androidx.work.Constraints.Builder().setRequiredNetworkType(androidx.work.NetworkType.CONNECTED).build()
        val syncRequest = androidx.work.OneTimeWorkRequestBuilder<com.example.data.sync.AcademicSyncWorker>().setConstraints(constraints).build()
        androidx.work.WorkManager.getInstance(context).enqueue(syncRequest)
    }

    override suspend fun deleteAnnouncement(id: String) {
        dao.deleteAnnouncement(id)
    }

    override fun getStudyMaterialsForCourse(courseId: String): Flow<List<StudyMaterial>> =
        dao.getStudyMaterialsForCourse(courseId).map { list -> list.map { it.toDomain() } }

    override suspend fun createStudyMaterial(material: StudyMaterial) {
        dao.insertStudyMaterial(material.toEntity())

        val constraints = androidx.work.Constraints.Builder().setRequiredNetworkType(androidx.work.NetworkType.CONNECTED).build()
        val syncRequest = androidx.work.OneTimeWorkRequestBuilder<com.example.data.sync.AcademicSyncWorker>().setConstraints(constraints).build()
        androidx.work.WorkManager.getInstance(context).enqueue(syncRequest)
    }

    override suspend fun deleteStudyMaterial(id: String) {
        dao.deleteStudyMaterial(id)
    }

    override fun getAssignmentsForCourse(courseId: String): Flow<List<Assignment>> =
        dao.getAssignmentsForCourse(courseId).map { list -> list.map { it.toDomain() } }

    override suspend fun createAssignment(assignment: Assignment) {
        dao.insertAssignment(assignment.toEntity())

        val constraints = androidx.work.Constraints.Builder().setRequiredNetworkType(androidx.work.NetworkType.CONNECTED).build()
        val syncRequest = androidx.work.OneTimeWorkRequestBuilder<com.example.data.sync.AcademicSyncWorker>().setConstraints(constraints).build()
        androidx.work.WorkManager.getInstance(context).enqueue(syncRequest)
    }

    override suspend fun deleteAssignment(id: String) {
        dao.deleteAssignment(id)
    }

    override fun getStudentCompletions(studentId: String): Flow<List<AssignmentCompletion>> =
        dao.getCompletionsForStudent(studentId).map { list -> list.map { it.toDomain() } }

    override suspend fun markAssignmentCompleted(completion: AssignmentCompletion) {
        dao.insertAssignmentCompletion(completion.toEntity())

        val constraints = androidx.work.Constraints.Builder().setRequiredNetworkType(androidx.work.NetworkType.CONNECTED).build()
        val syncRequest = androidx.work.OneTimeWorkRequestBuilder<com.example.data.sync.AcademicSyncWorker>().setConstraints(constraints).build()
        androidx.work.WorkManager.getInstance(context).enqueue(syncRequest)
    }
    
    override suspend fun getAssignmentCompletion(assignmentId: String, studentId: String): AssignmentCompletion? {
        return dao.getAssignmentCompletion(assignmentId, studentId)?.toDomain()
    }

    override fun getNotificationsForUser(userId: String): Flow<List<Notification>> =
        dao.getNotificationsForUser(userId).map { list -> list.map { it.toDomain() } }

    override suspend fun markNotificationAsRead(id: String) {
        dao.markNotificationAsRead(id)
    }
    
    override suspend fun createNotification(notification: Notification) {
        dao.insertNotification(notification.toEntity())
    }
}
