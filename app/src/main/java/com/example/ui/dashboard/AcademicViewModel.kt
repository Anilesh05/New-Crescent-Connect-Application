package com.example.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SessionManager
import com.example.domain.model.Announcement
import com.example.domain.model.Assignment
import com.example.domain.model.StudyMaterial
import com.example.domain.repository.AcademicRepository
import com.example.domain.repository.FileStorageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AcademicUiState(
    val announcements: List<Announcement> = emptyList(),
    val assignments: List<Assignment> = emptyList(),
    val studyMaterials: List<StudyMaterial> = emptyList()
)

class AcademicViewModel(
    
    private val academicRepository: AcademicRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AcademicUiState())
    val uiState: StateFlow<AcademicUiState> = _uiState.asStateFlow()

    fun loadCourseData(courseId: String) {
        viewModelScope.launch {
            launch {
                academicRepository.getAnnouncementsForCourse(courseId).collect {
                    _uiState.value = _uiState.value.copy(announcements = it)
                }
            }
            launch {
                academicRepository.getAssignmentsForCourse(courseId).collect {
                    _uiState.value = _uiState.value.copy(assignments = it)
                }
            }
            launch {
                academicRepository.getStudyMaterialsForCourse(courseId).collect {
                    _uiState.value = _uiState.value.copy(studyMaterials = it)
                }
            }
        }
    }
}
