with open('app/src/main/java/com/example/ui/dashboard/StaffAttendanceViewModel.kt', 'r') as f:
    content = f.read()

import_statement = """import java.util.UUID
"""

new_func = """
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
"""

content = content.replace("}\n}", "}" + new_func + "\n}")

with open('app/src/main/java/com/example/ui/dashboard/StaffAttendanceViewModel.kt', 'w') as f:
    f.write(import_statement + content)

