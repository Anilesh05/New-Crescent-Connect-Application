package com.example.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SessionManager
import com.example.domain.model.Course
import com.example.domain.model.User
import com.example.domain.repository.CourseRepository
import com.example.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StaffDashboardUiState(
    val user: User? = null,
    val assignedCourses: List<Course> = emptyList(),
    val isLoading: Boolean = true
)

class StaffDashboardViewModel(
    private val userRepository: UserRepository,
    private val courseRepository: CourseRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(StaffDashboardUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val userId = sessionManager.userIdFlow.firstOrNull()
            if (userId != null) {
                val user = userRepository.getUserById(userId)
                
                courseRepository.getCoursesForFaculty(userId).collect { courses ->
                    _uiState.update { 
                        it.copy(
                            user = user,
                            assignedCourses = courses,
                            isLoading = false
                        )
                    }
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
        }
    }
}
