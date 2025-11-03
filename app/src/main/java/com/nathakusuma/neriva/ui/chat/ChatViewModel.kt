package com.nathakusuma.neriva.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nathakusuma.neriva.data.model.ChatMessage
import com.nathakusuma.neriva.data.model.Pet
import com.nathakusuma.neriva.data.model.Result
import com.nathakusuma.neriva.data.repository.ChatRepository
import com.nathakusuma.neriva.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Chat screen
 * Manages chat messages and pet information
 */
class ChatViewModel(
    private val chatRepository: ChatRepository = ChatRepository.getInstance(),
    private val userRepository: UserRepository = UserRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadChatData()
    }

    /**
     * Load chat messages and pet information
     */
    private fun loadChatData() {
        viewModelScope.launch {
            // Load pet information
            userRepository.getUserPet().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            pet = result.data,
                            isLoading = false
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = result.exception.message,
                            isLoading = false
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            // Load chat messages
            chatRepository.getChatMessages().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            messages = result.data,
                            isLoading = false
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = result.exception.message,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    /**
     * Send a message to the pet
     */
    fun sendMessage(message: String) {
        viewModelScope.launch {
            chatRepository.sendMessage(message).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isSending = true)
                    }
                    is Result.Success -> {
                        // Add the sent message to the list
                        val updatedMessages = _uiState.value.messages + result.data
                        _uiState.value = _uiState.value.copy(
                            messages = updatedMessages,
                            isSending = false
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = result.exception.message,
                            isSending = false
                        )
                    }
                }
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

/**
 * UI state for the Chat screen
 */
data class ChatUiState(
    val pet: Pet? = null,
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val errorMessage: String? = null
)
