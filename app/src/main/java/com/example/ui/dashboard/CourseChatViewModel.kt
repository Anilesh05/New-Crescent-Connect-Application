package com.example.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SessionManager
import com.example.domain.model.Conversation
import com.example.domain.model.Message
import com.example.domain.model.Role
import com.example.domain.repository.ChatRepository
import com.example.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID

data class ChatUiState(
    val conversation: Conversation? = null,
    val messages: List<Message> = emptyList(),
    val currentUserId: String = "",
    val currentUserRole: Role = Role.STUDENT
)

class CourseChatViewModel(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val userId = sessionManager.userIdFlow.firstOrNull() ?: ""
            val user = userRepository.getUserById(userId)
            _uiState.value = _uiState.value.copy(
                currentUserId = userId,
                currentUserRole = user?.role ?: Role.STUDENT
            )
        }
    }

    fun loadCourseChat(courseId: String) {
        viewModelScope.launch {
            val conv = chatRepository.getCourseConversation(courseId)
            _uiState.value = _uiState.value.copy(conversation = conv)
            chatRepository.getMessages(conv.id).collect { msgs ->
                _uiState.value = _uiState.value.copy(messages = msgs)
            }
        }
    }

    fun loadDirectChat(staffId: String) {
        viewModelScope.launch {
            val userId = _uiState.value.currentUserId
            val conv = chatRepository.getDirectConversation(userId, staffId)
            _uiState.value = _uiState.value.copy(conversation = conv)
            chatRepository.getMessages(conv.id).collect { msgs ->
                _uiState.value = _uiState.value.copy(messages = msgs)
            }
        }
    }

    fun sendMessage(text: String) {
        val convId = _uiState.value.conversation?.id ?: return
        val senderId = _uiState.value.currentUserId
        val role = _uiState.value.currentUserRole
        
        viewModelScope.launch {
            val msg = Message(
                id = UUID.randomUUID().toString(),
                conversationId = convId,
                senderId = senderId,
                senderRole = role.name,
                messageText = text,
                timestamp = System.currentTimeMillis(),
                attachmentUri = null,
                isRead = false
            )
            chatRepository.sendMessage(msg)
        }
    }
}
