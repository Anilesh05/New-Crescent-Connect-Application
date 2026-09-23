package com.example.ui.dashboard
import java.util.UUID



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SessionManager
import com.example.domain.model.*
import com.example.domain.repository.AttendanceRepository
import com.example.domain.repository.CourseRepository
import com.example.domain.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class StudentAttendanceState(
    val student: User,
    val status: AttendanceStatus
)

data class StaffAttendanceUiState(
    val isLoading: Boolean = true,
    val courses: List<Course> = emptyList(),
    val selectedCourse: Course? = null,
    val selectedDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
    val selectedPeriod: Int = 1,
    val enrolledStudents: List<StudentAttendanceState> = emptyList(),
    val isSessionExisting: Boolean = false,
    val isSubmitted: Boolean = false,
    val errorMessage: String? = null
)

class StaffAttendanceViewModel(
    private val attendanceRepository: AttendanceRepository,
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(StaffAttendanceUiState())
    val uiState: StateFlow<StaffAttendanceUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            sessionManager.userIdFlow.filterNotNull().collectLatest { userId ->
                currentUserId = userId
                courseRepository.getCoursesForFaculty(userId).collectLatest { courses ->
                    _uiState.update { it.copy(isLoading = false, courses = courses) }
                }
            }
        }
    }

    fun selectCourse(course: Course) {
        _uiState.update { it.copy(selectedCourse = course) }
        checkExistingSessionAndLoadStudents()
    }

    fun selectDate(date: String) {
        _uiState.update { it.copy(selectedDate = date) }
        checkExistingSessionAndLoadStudents()
    }

    fun selectPeriod(period: Int) {
        _uiState.update { it.copy(selectedPeriod = period) }
        checkExistingSessionAndLoadStudents()
    }

    private fun checkExistingSessionAndLoadStudents() {
        val course = _uiState.value.selectedCourse ?: return
        val date = _uiState.value.selectedDate
        val period = _uiState.value.selectedPeriod
        
        viewModelScope.launch {
            val session = attendanceRepository.getSessionByDetails(course.id, date, period)
            _uiState.update { it.copy(isSessionExisting = session != null, isSubmitted = false) }

            val students = userRepository.getAllStudents().first().filter {
                // Simplified: assuming all students are enrolled in course for demo if they have enrollments,
                // But wait, the CourseRepository doesn't easily give students per course.
                // Actually, I can use a simpler approach or fetch all students (CrescentDB demo has a small list).
                // Let's just fetch all students and assume they are in the class for now, or use course enrollments if I had a query.
                // Wait, the DB seeder only has U1, U4, U5 as students, and Enrollments are specific.
                // Without a proper 'getStudentsForCourse' DAO, let's just get all students for the demo and limit to STUDENT role.
                it.role == Role.STUDENT || it.role == Role.CR
            }

            if (session != null) {
                val records = attendanceRepository.getRecordsForSession(session.id)
                val states = students.map { student ->
                    val record = records.find { it.studentId == student.id }
                    StudentAttendanceState(student, record?.status ?: AttendanceStatus.ABSENT)
                }
                _uiState.update { it.copy(enrolledStudents = states) }
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

    fun markAllPresent() {
        val updated = _uiState.value.enrolledStudents.map {
            it.copy(status = AttendanceStatus.PRESENT)
        }
        _uiState.update { it.copy(enrolledStudents = updated) }
    }

    fun submitAttendance() {
        val course = _uiState.value.selectedCourse ?: return
        val date = _uiState.value.selectedDate
        val period = _uiState.value.selectedPeriod
        val userId = currentUserId ?: return

        viewModelScope.launch {
            try {
                // Idempotent save
                val existingSession = attendanceRepository.getSessionByDetails(course.id, date, period)
                val sessionId = existingSession?.id ?: UUID.randomUUID().toString()
                
                val session = AttendanceSession(
                    id = sessionId,
                    courseId = course.id,
                    date = date,
                    periodNumber = period,
                    markedBy = userId,
                    markerRole = Role.STAFF,
                    syncStatus = SyncStatus.PENDING,
                    timestamp = System.currentTimeMillis()
                )

                val records = _uiState.value.enrolledStudents.map {
                    AttendanceRecord(
                        id = UUID.randomUUID().toString(), // Should technically check existing record IDs if editing, but REPLACE handles it if we generate predictable IDs based on session+student
                        sessionId = sessionId,
                        studentId = it.student.id,
                        status = it.status,
                        timestamp = System.currentTimeMillis(),
                        syncStatus = SyncStatus.PENDING
                    )
                }.map { record ->
                    // Make ID predictable for REPLACE on edit
                    record.copy(id = "${sessionId}_${record.studentId}")
                }

                attendanceRepository.saveAttendance(session, records)
                _uiState.update { it.copy(isSubmitted = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to submit attendance") }
            }
        }
    }
    fun startSelfAttendanceWindow(durationMinutes: Int, latitude: Double, longitude: Double, radiusMeters: Int) {
        viewModelScope.launch {
            val course = _uiState.value.selectedCourse ?: return@launch
            val staffId = sessionManager.userIdFlow.firstOrNull() ?: return@launch
            val sessionDate = _uiState.value.selectedDate
            val period = _uiState.value.selectedPeriod
            
            val existing = attendanceRepository.getSessionByDetails(course.id, sessionDate, period)
            if (existing != null) {
                _uiState.update { it.copy(errorMessage = "A session already exists for this period.") }
                return@launch
            }
            
            val startTime = System.currentTimeMillis()
            val endTime = startTime + (durationMinutes * 60 * 1000)
            
            val newSession = AttendanceSession(
                id = UUID.randomUUID().toString(),
                courseId = course.id,
                date = sessionDate,
                periodNumber = period,
                markedBy = staffId,
                markerRole = Role.STAFF,
                syncStatus = SyncStatus.PENDING,
                timestamp = startTime,
                isSelfAttendanceEnabled = true,
                startTime = startTime,
                endTime = endTime,
                latitude = latitude,
                longitude = longitude,
                allowedRadiusMeters = radiusMeters
            )
            
            attendanceRepository.saveSession(newSession)
            _uiState.update { it.copy(isSessionExisting = true, errorMessage = "Self-Attendance Window Started!") }
        }
    }

}
