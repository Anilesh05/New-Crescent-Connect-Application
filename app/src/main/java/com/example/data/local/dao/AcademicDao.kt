package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.AnnouncementEntity
import com.example.data.local.entity.StudyMaterialEntity
import com.example.data.local.entity.AssignmentEntity
import com.example.data.local.entity.AssignmentCompletionEntity
import com.example.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AcademicDao {
    @Query("SELECT * FROM announcements WHERE courseId = :courseId ORDER BY createdAt DESC")
    fun getAnnouncementsForCourse(courseId: String): Flow<List<AnnouncementEntity>>
    
    @Query("SELECT * FROM announcements WHERE syncStatus = 'PENDING'")
    suspend fun getPendingAnnouncements(): List<AnnouncementEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncements(announcements: List<AnnouncementEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: AnnouncementEntity)
    
    @Query("UPDATE announcements SET syncStatus = :status WHERE id = :id")
    suspend fun updateAnnouncementSyncStatus(id: String, status: String)
    
    @Query("DELETE FROM announcements WHERE id = :id")
    suspend fun deleteAnnouncement(id: String)

    @Query("SELECT * FROM study_materials WHERE courseId = :courseId ORDER BY createdAt DESC")
    fun getStudyMaterialsForCourse(courseId: String): Flow<List<StudyMaterialEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyMaterials(materials: List<StudyMaterialEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyMaterial(material: StudyMaterialEntity)
    
    @Query("DELETE FROM study_materials WHERE id = :id")
    suspend fun deleteStudyMaterial(id: String)
    
    @Query("SELECT * FROM assignments WHERE courseId = :courseId ORDER BY createdAt DESC")
    fun getAssignmentsForCourse(courseId: String): Flow<List<AssignmentEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignments(assignments: List<AssignmentEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: AssignmentEntity)
    
    @Query("DELETE FROM assignments WHERE id = :id")
    suspend fun deleteAssignment(id: String)
    
    @Query("SELECT * FROM assignment_completions WHERE assignmentId = :assignmentId AND studentId = :studentId LIMIT 1")
    suspend fun getAssignmentCompletion(assignmentId: String, studentId: String): AssignmentCompletionEntity?
    
    @Query("SELECT * FROM assignment_completions WHERE studentId = :studentId")
    fun getCompletionsForStudent(studentId: String): Flow<List<AssignmentCompletionEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignmentCompletions(completions: List<AssignmentCompletionEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignmentCompletion(completion: AssignmentCompletionEntity)
    
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificationsForUser(userId: String): Flow<List<NotificationEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)
    
    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: String)
}
