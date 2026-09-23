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
    val timestamp: Long,
    val isSelfAttendanceEnabled: Boolean = false,
    val startTime: Long? = null,
    val endTime: Long? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val allowedRadiusMeters: Int? = null
)

data class AttendanceRecord(
    val id: String,
    val sessionId: String,
    val studentId: String,
    val status: AttendanceStatus,
    val timestamp: Long,
    val syncStatus: SyncStatus,
    val verificationMethod: String = "MANUAL",
    val verifiedAt: Long? = null,
    val distanceFromCenter: Float? = null,
    val locationAccuracy: Float? = null
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
