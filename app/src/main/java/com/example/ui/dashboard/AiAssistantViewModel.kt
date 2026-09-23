package com.example.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SessionManager
import com.example.domain.model.AttendanceRecord
import com.example.domain.model.AttendanceStatus
import com.example.domain.repository.AttendanceRepository
import com.example.domain.repository.CourseRepository
import com.example.domain.repository.AiService
import com.example.domain.model.User
import com.example.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AiMessage(
    val text: String,
    val isUser: Boolean,
    val isError: Boolean = false,
    val isLoading: Boolean = false
)

class AiAssistantViewModel(
    private val sessionManager: SessionManager,
    private val userRepository: UserRepository,
    private val courseRepository: CourseRepository,
    private val attendanceRepository: AttendanceRepository,
    private val aiService: AiService
) : ViewModel() {

    private val _messages = MutableStateFlow<List<AiMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    private var currentUser: User? = null

    init {
        viewModelScope.launch {
            val userId = sessionManager.userIdFlow.firstOrNull()
            if (userId != null) {
                currentUser = userRepository.getUserById(userId)
            }
            _messages.update { 
                it + AiMessage("Hello! I am Crescent AI, your academic assistant. How can I help you today?", isUser = false)
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        _messages.update { it + AiMessage(text, isUser = true) }
        _messages.update { it + AiMessage("Thinking...", isUser = false, isLoading = true) }

        viewModelScope.launch {
            val context = buildContext(text)
            val prompt = """
                You are Crescent AI, an academic assistant.
                Answer the user's question safely and concisely.
                Use the following context if it is relevant. If the context contains academic data, format the response using:
                ANSWER
                DATA USED
                CALCULATION / REASON
                RECOMMENDATION
                
                Context:
                $context
                
                Question: $text
            """.trimIndent()
            
            val response = aiService.generateResponse(prompt)
            
            _messages.update { current ->
                val newMessages = current.filterNot { it.isLoading }
                newMessages + AiMessage(response, isUser = false, isError = response.contains("OFFLINE / UNAVAILABLE"))
            }
        }
    }

    private suspend fun buildContext(query: String): String {
        val q = query.lowercase()
        val builder = java.lang.StringBuilder()
        builder.append("User Role: ${currentUser?.role?.name ?: "Unknown"}\n")
        builder.append("User Name: ${currentUser?.name ?: "Unknown"}\n")

        if (q.contains("attendance") || q.contains("classes") || q.contains("skip") || q.contains("risk")) {
            val userId = currentUser?.id ?: return builder.toString()
            val courses = courseRepository.getCoursesForStudent(userId).firstOrNull() ?: emptyList()
            val sessions = attendanceRepository.getAllSessions().firstOrNull() ?: emptyList()
            val records = attendanceRepository.getRecordsForStudent(userId).firstOrNull() ?: emptyList()

            builder.append("--- ATTENDANCE DATA ---\n")
            courses.forEach { course ->
                val courseSessions = sessions.filter { it.courseId == course.id }
                val sessionIds = courseSessions.map { it.id }.toSet()
                val relevantRecords = records.filter { it.sessionId in sessionIds }
                val totalClasses = sessionIds.size
                val presentClasses = relevantRecords.count { it.status == AttendanceStatus.PRESENT }
                val percentage = if (totalClasses > 0) (presentClasses * 100f) / totalClasses else 0f
                val requiredFor75 = if (percentage >= 75f || totalClasses == 0) 0 else (3 * totalClasses - 4 * presentClasses).coerceAtLeast(0)
                val impactIfMissedNext = if (totalClasses == 0) 0f else (presentClasses * 100f) / (totalClasses + 1)
                
                val risk = when {
                    totalClasses == 0 -> "SAFE"
                    percentage >= 75 -> "SAFE"
                    percentage >= 65 -> "WARNING"
                    else -> "CRITICAL"
                }
                
                builder.append("Course: ${course.subjectName} (${course.subjectCode})\n")
                builder.append("Classes Attended: $presentClasses / $totalClasses\n")
                builder.append("Percentage: ${String.format("%.1f", percentage)}%\n")
                builder.append("Risk Level: $risk\n")
                builder.append("Classes required for 75%: $requiredFor75\n")
                builder.append("Percentage if next class missed: ${String.format("%.1f", impactIfMissedNext)}%\n\n")
            }
        }

        if (q.contains("mark") || q.contains("score") || q.contains("target") || q.contains("percentage")) {
            // Mocking marks as requested for deterministic analysis if not present
            builder.append("--- MARKS DATA (Deterministic Analysis) ---\n")
            builder.append("Note: The marks system currently tracks standard configurable metrics.\n")
            builder.append("To calculate external mark needed for a target total:\n")
            builder.append("Formula: External Required = Target Total - Internal Marks\n")
            builder.append("Example current internal marks: DBMS: 34/50, Python: 40/50, Java: 25/50\n")
        }

        return builder.toString()
    }
}
