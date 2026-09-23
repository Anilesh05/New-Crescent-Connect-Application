package com.example.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SessionManager
import com.example.domain.model.AttendanceRecord
import com.example.domain.model.AttendanceStatus
import com.example.domain.model.Course
import com.example.domain.repository.AttendanceRepository
import com.example.domain.repository.CourseRepository
import com.example.domain.model.AttendanceSession
import com.example.domain.model.SyncStatus
import java.util.UUID
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.ceil

data class CourseAttendanceStat(
    val courseId: String,
    val subjectCode: String,
    val subjectName: String,
    val totalClasses: Int,
    val presentClasses: Int,
    val percentage: Float
) {
    fun requiredFor75(): Int {
        if (percentage >= 75f || totalClasses == 0) return 0
        return (3 * totalClasses - 4 * presentClasses).coerceAtLeast(0)
    }

    fun impactOfAbsence(): Float {
        if (totalClasses == 0) return 0f
        return (presentClasses * 100f) / (totalClasses + 1)
    }
}

data class ActiveSelfAttendanceSession(
    val session: AttendanceSession,
    val courseName: String,
    val hasAlreadyMarked: Boolean
)

data class StudentAttendanceUiState(
    val isLoading: Boolean = true,
    val overallPercentage: Float = 0f,
    val courseStats: List<CourseAttendanceStat> = emptyList(),
    val activeSessions: List<ActiveSelfAttendanceSession> = emptyList(),
    val attendanceMarkResult: String? = null
)

class StudentAttendanceViewModel(
    private val attendanceRepository: AttendanceRepository,
    private val courseRepository: CourseRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentAttendanceUiState())
    val uiState: StateFlow<StudentAttendanceUiState> = _uiState.asStateFlow()

    init {
        loadAttendance()
    }

    private fun loadAttendance() {
        viewModelScope.launch {
            sessionManager.userIdFlow.filterNotNull().collectLatest { userId ->
                combine(
                    courseRepository.getCoursesForStudent(userId),
                    attendanceRepository.getRecordsForStudent(userId),
                    attendanceRepository.getAllSessions()
                ) { courses, studentRecords, allSessions ->
                    
                    val stats = courses.map { course ->
                        val courseSessions = allSessions.filter { it.courseId == course.id }
                        val courseSessionIds = courseSessions.map { it.id }.toSet()
                        
                        val relevantRecords = studentRecords.filter { it.sessionId in courseSessionIds }
                        val totalClasses = courseSessionIds.size
                        val presentClasses = relevantRecords.count { it.status == AttendanceStatus.PRESENT }
                        
                        val percentage = if (totalClasses > 0) (presentClasses * 100f) / totalClasses else 0f
                        
                        CourseAttendanceStat(
                            courseId = course.id,
                            subjectCode = course.subjectCode,
                            subjectName = course.subjectName,
                            totalClasses = totalClasses,
                            presentClasses = presentClasses,
                            percentage = percentage
                        )
                    }

                    val totalPresent = stats.sumOf { it.presentClasses }
                    val totalConducted = stats.sumOf { it.totalClasses }
                    val overallPercentage = if (totalConducted > 0) (totalPresent * 100f) / totalConducted else 0f

                    val currentTime = System.currentTimeMillis()
                    val activeSessions = allSessions.filter { 
                        it.isSelfAttendanceEnabled && 
                        it.startTime != null && currentTime >= it.startTime &&
                        it.endTime != null && currentTime <= it.endTime 
                    }.mapNotNull { session ->
                        val course = courses.find { it.id == session.courseId } ?: return@mapNotNull null
                        val hasMarked = studentRecords.any { it.sessionId == session.id }
                        ActiveSelfAttendanceSession(session, course.subjectName, hasMarked)
                    }

                    StudentAttendanceUiState(
                        isLoading = false,
                        overallPercentage = overallPercentage,
                        courseStats = stats,
                        activeSessions = activeSessions
                    )
                }.collect { state ->
                    _uiState.value = state
                }
            }
        }
    }
    
    fun markSelfAttendance(session: AttendanceSession, distance: Float, accuracy: Float, isMock: Boolean) {
        viewModelScope.launch {
            if (isMock) {
                _uiState.update { it.copy(attendanceMarkResult = "Error: Mock location detected.") }
                return@launch
            }
            if (session.allowedRadiusMeters != null && distance > session.allowedRadiusMeters) {
                _uiState.update { it.copy(attendanceMarkResult = "Error: You are outside the allowed location radius (${distance.toInt()} meters away).") }
                return@launch
            }
            
            val currentTime = System.currentTimeMillis()
            if ((session.startTime != null && currentTime < session.startTime) || (session.endTime != null && currentTime > session.endTime)) {
                _uiState.update { it.copy(attendanceMarkResult = "Error: Attendance window is closed.") }
                return@launch
            }

            val userId = sessionManager.userIdFlow.firstOrNull() ?: return@launch
            val record = AttendanceRecord(
                id = UUID.randomUUID().toString(),
                sessionId = session.id,
                studentId = userId,
                status = AttendanceStatus.PRESENT,
                timestamp = currentTime,
                syncStatus = SyncStatus.PENDING,
                verificationMethod = "GPS",
                verifiedAt = currentTime,
                distanceFromCenter = distance,
                locationAccuracy = accuracy
            )
            attendanceRepository.saveRecord(record)
            _uiState.update { it.copy(attendanceMarkResult = "Success: Attendance marked successfully! (Distance: ${distance.toInt()}m)") }
        }
    }
    
    fun clearResult() {
        _uiState.update { it.copy(attendanceMarkResult = null) }
    }
}
