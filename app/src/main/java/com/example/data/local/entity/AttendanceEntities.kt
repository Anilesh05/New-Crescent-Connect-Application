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
    val timestamp: Long,
    val isSelfAttendanceEnabled: Boolean = false,
    val startTime: Long? = null,
    val endTime: Long? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val allowedRadiusMeters: Int? = null
)

@Entity(tableName = "attendance_records")
data class AttendanceRecordEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val studentId: String,
    val status: String,
    val timestamp: Long,
    val syncStatus: String,
    val verificationMethod: String = "MANUAL",
    val verifiedAt: Long? = null,
    val distanceFromCenter: Float? = null,
    val locationAccuracy: Float? = null
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
    val isActive: Boolean,
    val syncStatus: String = "SYNCED"
)
