package com.example.data.repository

import com.example.data.local.dao.AttendanceDao
import com.example.data.local.entity.AttendanceRecordEntity
import com.example.data.local.entity.AttendanceSessionEntity
import com.example.data.local.entity.CRPermissionEntity
import com.example.domain.model.AttendanceRecord
import com.example.domain.model.AttendanceSession
import com.example.domain.model.AttendanceStatus
import com.example.domain.model.CRPermission
import com.example.domain.model.Role
import com.example.domain.model.SyncStatus
import com.example.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Constraints
import androidx.work.NetworkType
import com.example.data.sync.AttendanceSyncWorker

class AttendanceRepositoryImpl(
    private val attendanceDao: AttendanceDao,
    private val context: Context
) : AttendanceRepository {

    
    override suspend fun saveSession(session: AttendanceSession) {
        attendanceDao.insertSession(session.toEntity())
    }

    override suspend fun saveRecord(record: AttendanceRecord) {
        attendanceDao.insertRecords(listOf(record.toEntity()))
    }

    override suspend fun saveAttendance(session: AttendanceSession, records: List<AttendanceRecord>) {
        attendanceDao.insertSession(session.toEntity())
        attendanceDao.insertRecords(records.map { it.toEntity() })
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val syncRequest = OneTimeWorkRequestBuilder<AttendanceSyncWorker>().setConstraints(constraints).build()
        WorkManager.getInstance(context).enqueue(syncRequest)
    }

    override fun getSessionsForCourse(courseId: String): Flow<List<AttendanceSession>> {
        return attendanceDao.getSessionsForCourse(courseId).map { list -> list.map { it.toDomain() } }
    }
    
    override fun getAllSessions(): Flow<List<AttendanceSession>> {
        return attendanceDao.getAllSessions().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getSessionByDetails(courseId: String, date: String, periodNumber: Int): AttendanceSession? {
        return attendanceDao.getSessionByDetails(courseId, date, periodNumber)?.toDomain()
    }

    override fun getRecordsForSessionFlow(sessionId: String): Flow<List<AttendanceRecord>> {
        return attendanceDao.getRecordsForSessionFlow(sessionId).map { list -> list.map { it.toDomain() } }
    }
    
    override suspend fun getRecordsForSession(sessionId: String): List<AttendanceRecord> {
        return attendanceDao.getRecordsForSession(sessionId).map { it.toDomain() }
    }

    override fun getRecordsForStudent(studentId: String): Flow<List<AttendanceRecord>> {
        return attendanceDao.getRecordsForStudent(studentId).map { list -> list.map { it.toDomain() } }
    }
    
    override fun getAllRecords(): Flow<List<AttendanceRecord>> {
        return attendanceDao.getAllRecords().map { list -> list.map { it.toDomain() } }
    }

    override fun getActivePermissionsForCR(crId: String): Flow<List<CRPermission>> {
        return attendanceDao.getActivePermissionsForCR(crId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun grantCRPermission(permission: CRPermission) {
        attendanceDao.insertPermission(permission.toEntity())
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val syncRequest = OneTimeWorkRequestBuilder<AttendanceSyncWorker>().setConstraints(constraints).build()
        WorkManager.getInstance(context).enqueue(syncRequest)
    }
    
    override suspend fun getPermissionById(id: String): CRPermission? {
        return attendanceDao.getPermissionById(id)?.toDomain()
    }
}

// Extension functions for mapping
fun AttendanceSession.toEntity() = AttendanceSessionEntity(
    id = id,
    courseId = courseId,
    date = date,
    periodNumber = periodNumber,
    markedBy = markedBy,
    markerRole = markerRole.name,
    syncStatus = syncStatus.name,
    timestamp = timestamp,
    isSelfAttendanceEnabled = isSelfAttendanceEnabled,
    startTime = startTime,
    endTime = endTime,
    latitude = latitude,
    longitude = longitude,
    allowedRadiusMeters = allowedRadiusMeters
)

fun AttendanceSessionEntity.toDomain() = AttendanceSession(
    id = id,
    courseId = courseId,
    date = date,
    periodNumber = periodNumber,
    markedBy = markedBy,
    markerRole = Role.valueOf(markerRole),
    syncStatus = SyncStatus.valueOf(syncStatus),
    timestamp = timestamp,
    isSelfAttendanceEnabled = isSelfAttendanceEnabled,
    startTime = startTime,
    endTime = endTime,
    latitude = latitude,
    longitude = longitude,
    allowedRadiusMeters = allowedRadiusMeters
)

fun AttendanceRecord.toEntity() = AttendanceRecordEntity(
    id = id,
    sessionId = sessionId,
    studentId = studentId,
    status = status.name,
    timestamp = timestamp,
    syncStatus = syncStatus.name,
    verificationMethod = verificationMethod,
    verifiedAt = verifiedAt,
    distanceFromCenter = distanceFromCenter,
    locationAccuracy = locationAccuracy
)

fun AttendanceRecordEntity.toDomain() = AttendanceRecord(
    id = id,
    sessionId = sessionId,
    studentId = studentId,
    status = AttendanceStatus.valueOf(status),
    timestamp = timestamp,
    syncStatus = SyncStatus.valueOf(syncStatus),
    verificationMethod = verificationMethod,
    verifiedAt = verifiedAt,
    distanceFromCenter = distanceFromCenter,
    locationAccuracy = locationAccuracy
)

fun CRPermission.toEntity() = CRPermissionEntity(
    id = id,
    crId = crId,
    courseId = courseId,
    date = date,
    periodNumber = periodNumber,
    validFrom = validFrom,
    validUntil = validUntil,
    grantedBy = grantedBy,
    isActive = isActive,
    syncStatus = "PENDING"
)

fun CRPermissionEntity.toDomain() = CRPermission(
    id = id,
    crId = crId,
    courseId = courseId,
    date = date,
    periodNumber = periodNumber,
    validFrom = validFrom,
    validUntil = validUntil,
    grantedBy = grantedBy,
    isActive = isActive
)
