package com.example.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SessionManager
import com.example.domain.model.*
import com.example.domain.repository.AttendanceRepository
import com.example.domain.repository.CourseRepository
import com.example.domain.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class CRAttendanceUiState(
    val isLoading: Boolean = true,
    val activePermissions: List<CRPermission> = emptyList(),
    val selectedPermission: CRPermission? = null,
    val course: Course? = null,
    val enrolledStudents: List<StudentAttendanceState> = emptyList(),
    val isSubmitted: Boolean = false,
    val errorMessage: String? = null
)

class CRAttendanceViewModel(
    private val attendanceRepository: AttendanceRepository,
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CRAttendanceUiState())
    val uiState: StateFlow<CRAttendanceUiState> = _uiState.asStateFlow()
    private var currentUserId: String? = null

    init {
        loadPermissions()
    }

    private fun loadPermissions() {
        viewModelScope.launch {
            sessionManager.userIdFlow.filterNotNull().collectLatest { userId ->
                currentUserId = userId
                attendanceRepository.getActivePermissionsForCR(userId).collectLatest { permissions ->
                    val now = System.currentTimeMillis()
                    val validPermissions = permissions.filter { it.isActive && now in it.validFrom..it.validUntil }
                    _uiState.update { it.copy(isLoading = false, activePermissions = validPermissions) }
                }
            }
        }
    }

    fun selectPermission(permission: CRPermission) {
        _uiState.update { it.copy(selectedPermission = permission, isSubmitted = false, errorMessage = null) }
        
        viewModelScope.launch {
            // Check if valid
            val now = System.currentTimeMillis()
            if (!permission.isActive || now !in permission.validFrom..permission.validUntil) {
                _uiState.update { it.copy(errorMessage = "Attendance permission is no longer active.") }
                return@launch
            }

            // Load students (simplified like Staff for demo)
            val students = userRepository.getAllStudents().first().filter {
                it.role == Role.STUDENT || it.role == Role.CR
            }

            // Load existing session if any (CR shouldn't normally override, but for safety)
            val session = attendanceRepository.getSessionByDetails(permission.courseId, permission.date, permission.periodNumber)
            
            if (session != null) {
                _uiState.update { it.copy(errorMessage = "Attendance has already been recorded for this period.") }
            } else {
                val states = students.map { student ->
                    StudentAttendanceState(student, AttendanceStatus.PRESENT)
                }
                _uiState.update { it.copy(enrolledStudents = states) }
            }
        }
    }
    
    fun markStudent(studentId: String, status: AttendanceStatus) {
        val updated = _uiState.value.enrolledStudents.map {
            if (it.student.id == studentId) it.copy(status = status) else it
        }
        _uiState.update { it.copy(enrolledStudents = updated) }
    }
    
    fun submitAttendance() {
        val permission = _uiState.value.selectedPermission ?: return
        val userId = currentUserId ?: return

        viewModelScope.launch {
            try {
                // Idempotent save
                val existingSession = attendanceRepository.getSessionByDetails(permission.courseId, permission.date, permission.periodNumber)
                if (existingSession != null) {
                    _uiState.update { it.copy(errorMessage = "Attendance already recorded.") }
                    return@launch
                }
                
                val sessionId = UUID.randomUUID().toString()
                
                val session = AttendanceSession(
                    id = sessionId,
                    courseId = permission.courseId,
                    date = permission.date,
                    periodNumber = permission.periodNumber,
                    markedBy = userId,
                    markerRole = Role.CR,
                    syncStatus = SyncStatus.PENDING,
                    timestamp = System.currentTimeMillis()
                )

                val records = _uiState.value.enrolledStudents.map {
                    AttendanceRecord(
                        id = "${sessionId}_${it.student.id}",
                        sessionId = sessionId,
                        studentId = it.student.id,
                        status = it.status,
                        timestamp = System.currentTimeMillis(),
                        syncStatus = SyncStatus.PENDING
                    )
                }

                attendanceRepository.saveAttendance(session, records)
                _uiState.update { it.copy(isSubmitted = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to submit attendance") }
            }
        }
    }
}
