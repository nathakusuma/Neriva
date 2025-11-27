package com.nathakusuma.neriva.ui.chat

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nathakusuma.neriva.data.model.ChatMessage
import com.nathakusuma.neriva.data.model.Pet
import com.nathakusuma.neriva.data.model.Result
import com.nathakusuma.neriva.data.model.SenderType
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
    private val context: Context,
    private val chatRepository: ChatRepository = ChatRepository.getInstance(context),
    private val userRepository: UserRepository = UserRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var currentPage = 0
    private val pageSize = 20

    init {
        loadChatData()
    }

    /**
     * Load chat messages and pet information
     * On first open: loads from local storage first
     * Only fetches from API if local storage is empty
     */
    private fun loadChatData() {
        loadPetInformation()
        loadChatMessages()
    }

    /**
     * Load pet information
     */
    private fun loadPetInformation() {
        viewModelScope.launch {
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
    }

    /**
     * Load initial chat messages (on app/screen open)
     * - First checks local storage/database
     * - Only fetches latest 20 messages from backend if local storage is empty
     * - This loads page 0 (bottom/latest messages)
     */
    private fun loadChatMessages() {
        viewModelScope.launch {
            chatRepository.getChatMessages(page = 0, size = pageSize, forceRefresh = false).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                    is Result.Success -> {
                        val messages = result.data
                        currentPage = 0
                        _uiState.value = _uiState.value.copy(
                            messages = messages.sortedBy { it.id },
                            isLoading = false,
                            canLoadMore = messages.size >= pageSize
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
     * Load more older messages (pagination)
     * - Called when user scrolls to top to see older messages
     * - Always fetches from backend to get messages beyond currently loaded ones
     * - Fetches 20 messages per page
     * - Merges new messages with existing ones and updates local storage
     */
    fun loadMoreMessages() {
        // Don't load more if already loading initial data, or if already loading more, or if can't load more
        if (_uiState.value.isLoading || _uiState.value.isLoadingMore || !_uiState.value.canLoadMore) {
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingMore = true)

            val nextPage = currentPage + 1

            chatRepository.getChatMessages(page = nextPage, size = pageSize, forceRefresh = true).collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            // Already set loading state above
                        }
                        is Result.Success -> {
                            val newMessages = result.data
                            if (newMessages.isNotEmpty()) {
                                val currentMessages = _uiState.value.messages
                                val updatedMessages = (currentMessages + newMessages)
                                    .distinctBy { it.id }
                                    .sortedBy { it.id }
                                currentPage = nextPage
                                _uiState.value = _uiState.value.copy(
                                    messages = updatedMessages,
                                    isLoadingMore = false,
                                    canLoadMore = newMessages.size >= pageSize
                                )
                            } else {
                                // No more messages available
                                _uiState.value = _uiState.value.copy(
                                    isLoadingMore = false,
                                    canLoadMore = false
                                )
                            }
                        }
                        is Result.Error -> {
                            _uiState.value = _uiState.value.copy(
                                errorMessage = result.exception.message,
                                isLoadingMore = false
                            )
                        }
                    }
                }
            }
    }


    /**
     * Send a message to the pet
     * Adds the sent message to local data immediately and appends pet reply from POST response
     */
    fun sendMessage(message: String) {
        // Create a temporary user message to show immediately in the UI
        val tempId = -(System.currentTimeMillis().toInt()) // Temporary negative ID based on timestamp
        val userMessage = ChatMessage(
            id = tempId,
            message = message,
            senderType = SenderType.USER,
            createdAt = "" // Temporary, will be replaced
        )

        // Add user message to UI immediately
        val updatedMessages = _uiState.value.messages + userMessage
        _uiState.value = _uiState.value.copy(
            messages = updatedMessages,
            isSending = true
        )

        viewModelScope.launch {
            chatRepository.sendMessage(message, userMessage).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        // Already showing as sending
                    }
                    is Result.Success -> {
                        // Pet reply received from POST response - append it to messages
                        val petReply = result.data
                        val currentMessages = _uiState.value.messages
                        val updatedMessagesWithPetReply = currentMessages + petReply
                        _uiState.value = _uiState.value.copy(
                            messages = updatedMessagesWithPetReply,
                            isSending = false
                        )
                    }
                    is Result.Error -> {
                        // Remove the user message on error
                        val messagesWithoutTemp = _uiState.value.messages.filter { it.id != tempId }
                        _uiState.value = _uiState.value.copy(
                            messages = messagesWithoutTemp,
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
    val isLoadingMore: Boolean = false,
    val isSending: Boolean = false,
    val canLoadMore: Boolean = true,
    val errorMessage: String? = null
)
