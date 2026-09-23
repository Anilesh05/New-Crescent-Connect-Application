# Create models
cat << 'INNER_EOF' > app/src/main/java/com/example/domain/model/Attendance.kt
package com.example.domain.model

enum class SyncStatus { PENDING, SYNCED, FAILED }
enum class AttendanceStatus { PRESENT, ABSENT }

data class AttendanceSession(
    val id: String,
    val courseId: String,
    val date: String,
    val periodNumber: Int,
    val markedBy: String,
    val markerRole: Role,
    val syncStatus: SyncStatus,
    val timestamp: Long
)

data class AttendanceRecord(
    val id: String,
    val sessionId: String,
    val studentId: String,
    val status: AttendanceStatus,
    val timestamp: Long,
    val syncStatus: SyncStatus
)

data class CRPermission(
    val id: String,
    val crId: String,
    val courseId: String,
    val date: String,
    val periodNumber: Int,
    val validFrom: Long,
    val validUntil: Long,
    val grantedBy: String,
    val isActive: Boolean
)
INNER_EOF

# Entities
cat << 'INNER_EOF' > app/src/main/java/com/example/data/local/entity/AttendanceEntities.kt
package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance_sessions")
data class AttendanceSessionEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val date: String,
    val periodNumber: Int,
    val markedBy: String,
    val markerRole: String,
    val syncStatus: String,
    val timestamp: Long
)

@Entity(tableName = "attendance_records")
data class AttendanceRecordEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val studentId: String,
    val status: String,
    val timestamp: Long,
    val syncStatus: String
)

@Entity(tableName = "cr_permissions")
data class CRPermissionEntity(
    @PrimaryKey val id: String,
    val crId: String,
    val courseId: String,
    val date: String,
    val periodNumber: Int,
    val validFrom: Long,
    val validUntil: Long,
    val grantedBy: String,
    val isActive: Boolean
)
INNER_EOF

# DAO
cat << 'INNER_EOF' > app/src/main/java/com/example/data/local/dao/AttendanceDao.kt
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
INNER_EOF

