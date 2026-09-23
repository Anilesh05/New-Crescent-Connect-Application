with open('app/src/main/java/com/example/ui/dashboard/StudentAttendanceViewModel.kt', 'r') as f:
    content = f.read()

import_statement = """import com.example.domain.model.AttendanceSession
import com.example.domain.model.SyncStatus
import java.util.UUID
"""

new_state = """data class ActiveSelfAttendanceSession(
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
)"""

content = content.replace("data class StudentAttendanceUiState(\n    val isLoading: Boolean = true,\n    val overallPercentage: Float = 0f,\n    val courseStats: List<CourseAttendanceStat> = emptyList()\n)", new_state)

content = content.replace("import kotlinx.coroutines.flow.*", import_statement + "import kotlinx.coroutines.flow.*")

load_logic_old = """                    val totalConducted = stats.sumOf { it.totalClasses }
                    val overallPercentage = if (totalConducted > 0) (totalPresent * 100f) / totalConducted else 0f
                    StudentAttendanceUiState(
                        isLoading = false,
                        overallPercentage = overallPercentage,
                        courseStats = stats
                    )"""

load_logic_new = """                    val totalConducted = stats.sumOf { it.totalClasses }
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
                    )"""

content = content.replace(load_logic_old, load_logic_new)

mark_func = """
    fun markSelfAttendance(session: AttendanceSession, studentId: String, distance: Float, accuracy: Float, isMock: Boolean) {
        viewModelScope.launch {
            if (isMock) {
                _uiState.update { it.copy(attendanceMarkResult = "Error: Mock location detected.") }
                return@launch
            }
            if (session.allowedRadiusMeters != null && distance > session.allowedRadiusMeters) {
                _uiState.update { it.copy(attendanceMarkResult = "Error: You are outside the allowed location radius ($distance meters away).") }
                return@launch
            }
            
            val currentTime = System.currentTimeMillis()
            if (session.startTime != null && currentTime < session.startTime || session.endTime != null && currentTime > session.endTime) {
                _uiState.update { it.copy(attendanceMarkResult = "Error: Attendance window is closed.") }
                return@launch
            }

            val record = AttendanceRecord(
                id = UUID.randomUUID().toString(),
                sessionId = session.id,
                studentId = studentId,
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
"""

content = content.replace("}\n}", "}" + mark_func + "}")

with open('app/src/main/java/com/example/ui/dashboard/StudentAttendanceViewModel.kt', 'w') as f:
    f.write(content)

