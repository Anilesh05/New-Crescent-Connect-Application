package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.AttendanceRecordEntity
import com.example.data.local.entity.AttendanceSessionEntity
import com.example.data.local.entity.CRPermissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance_sessions WHERE syncStatus = \"PENDING\"")
    suspend fun getPendingSessions(): List<AttendanceSessionEntity>

    @Query("UPDATE attendance_sessions SET syncStatus = :status WHERE id = :sessionId")
    suspend fun updateSessionSyncStatus(sessionId: String, status: String)

    @Query("UPDATE attendance_records SET syncStatus = :status WHERE id = :recordId")
    suspend fun updateRecordSyncStatus(recordId: String, status: String)

    @Query("SELECT * FROM cr_permissions WHERE syncStatus = \"PENDING\"")
    suspend fun getPendingPermissions(): List<CRPermissionEntity>
    
    @Query("SELECT * FROM attendance_records WHERE syncStatus = \"PENDING\"")
    suspend fun getPendingRecords(): List<AttendanceRecordEntity>

    @Query("UPDATE cr_permissions SET syncStatus = :status WHERE id = :permissionId")
    suspend fun updatePermissionSyncStatus(permissionId: String, status: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: AttendanceSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<AttendanceRecordEntity>)

    @Query("SELECT * FROM attendance_sessions WHERE courseId = :courseId ORDER BY timestamp DESC")
    fun getSessionsForCourse(courseId: String): Flow<List<AttendanceSessionEntity>>
    
    @Query("SELECT * FROM attendance_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<AttendanceSessionEntity>>

    @Query("SELECT * FROM attendance_sessions WHERE courseId = :courseId AND date = :date AND periodNumber = :periodNumber LIMIT 1")
    suspend fun getSessionByDetails(courseId: String, date: String, periodNumber: Int): AttendanceSessionEntity?

    @Query("SELECT * FROM attendance_records WHERE sessionId = :sessionId")
    fun getRecordsForSessionFlow(sessionId: String): Flow<List<AttendanceRecordEntity>>
    
    @Query("SELECT * FROM attendance_records WHERE sessionId = :sessionId")
    suspend fun getRecordsForSessionList(sessionId: String): List<AttendanceRecordEntity>

    @Query("SELECT * FROM attendance_records WHERE sessionId = :sessionId")
    suspend fun getRecordsForSession(sessionId: String): List<AttendanceRecordEntity>

    @Query("SELECT * FROM attendance_records WHERE studentId = :studentId")
    fun getRecordsForStudent(studentId: String): Flow<List<AttendanceRecordEntity>>
    
    @Query("SELECT * FROM attendance_records")
    fun getAllRecords(): Flow<List<AttendanceRecordEntity>>

    @Query("SELECT * FROM cr_permissions WHERE crId = :crId AND isActive = 1")
    fun getActivePermissionsForCR(crId: String): Flow<List<CRPermissionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPermission(permission: CRPermissionEntity)
    
    @Query("SELECT * FROM cr_permissions WHERE id = :id LIMIT 1")
    suspend fun getPermissionById(id: String): CRPermissionEntity?
}
