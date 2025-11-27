package com.nathakusuma.neriva.data.repository

import android.content.Context
import com.nathakusuma.neriva.data.local.ChatDataManager
import com.nathakusuma.neriva.data.model.ChatMessage
import com.nathakusuma.neriva.data.model.Result
import com.nathakusuma.neriva.data.model.SendMessageRequest
import com.nathakusuma.neriva.data.remote.ChatApiService
import com.nathakusuma.neriva.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repository for chat-related operations
 * Handles data operations and acts as a single source of truth
 */
class ChatRepository(
    private val apiService: ChatApiService = RetrofitClient.chatApiService,
    private val chatDataManager: ChatDataManager? = null
) {

    /**
     * Get chat messages with pet (paginated)
     * Returns a Flow of Result states (Loading, Success, Error)
     * 
     * Initial load (page 0, forceRefresh=false):
     *   - First checks local storage
     *   - Only fetches latest 20 messages from API if local storage is empty
     *
     * Pagination (page > 0 or forceRefresh=true):
     *   - Always fetches from API to get older messages
     *   - Merges with existing local storage
     *
     * @param page Page number (0-indexed, page 0 = latest messages)
     * @param size Number of messages per page (default 20)
     * @param forceRefresh Force fetching from API (used for pagination)
     */
    fun getChatMessages(page: Int = 0, size: Int = 20, forceRefresh: Boolean = false): Flow<Result<List<ChatMessage>>> = flow {
        try {
            emit(Result.Loading)
            
            // Initial load: Check local storage first
            if (page == 0 && !forceRefresh) {
                val localMessages = chatDataManager?.getMessages() ?: emptyList()

                if (localMessages.isNotEmpty()) {
                    // Return local data without fetching
                    emit(Result.Success(localMessages))
                    return@flow
                }

                // Local storage is empty, fetch latest 20 messages from API
                val response = apiService.getChatHistory(0, size)
                if (response.success) {
                    val messages = response.data
                    // Save to local storage (replace all)
                    chatDataManager?.saveMessages(messages)
                    emit(Result.Success(messages))
                } else {
                    emit(Result.Error(Exception(response.message)))
                }
            } else {
                // Pagination: Always fetch from API
                val response = apiService.getChatHistory(page, size)
                if (response.success) {
                    val newMessages = response.data

                    // Merge with existing local storage
                    if (newMessages.isNotEmpty()) {
                        chatDataManager?.addMessages(newMessages)
                    }

                    emit(Result.Success(newMessages))
                } else {
                    emit(Result.Error(Exception(response.message)))
                }
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
    
    /**
     * Fetch more messages from API for pagination
     * This is used when user scrolls beyond available data
     *
     * @param afterMessageId Fetch messages after this ID
     * @param size Number of messages to fetch
     */
    fun fetchMoreMessages(afterMessageId: Int, size: Int = 20): Flow<Result<List<ChatMessage>>> = flow {
        try {
            emit(Result.Loading)
            // This assumes your API supports fetching messages after a certain ID
            // If your API only supports page-based pagination, adjust accordingly
            val response = apiService.getChatHistory(0, size) // Adjust based on your API
            if (response.success) {
                val newMessages = response.data.filter { it.id > afterMessageId }
                // Add to local storage
                chatDataManager?.addMessages(newMessages)
                emit(Result.Success(newMessages))
            } else {
                emit(Result.Error(Exception(response.message)))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    /**
     * Send a message to pet
     * Returns a Flow of Result states (Loading, Success, Error)
     * The API response includes the pet's reply
     * Note: The backend stores the user's message automatically, we only save the pet reply locally
     *
     * @param message The message text to send
     * @param userMessage The user's temporary message object (for reference, not saved)
     * @return Flow with Result containing the pet's reply message
     */
    fun sendMessage(message: String, userMessage: ChatMessage): Flow<Result<ChatMessage>> = flow {
        try {
            emit(Result.Loading)
            val request = SendMessageRequest(message)
            val response = apiService.sendMessage(request)
            if (response.success) {
                val petReply = response.data
                // Only add pet reply to local storage
                // User message is stored by backend and will be fetched on next app launch
                chatDataManager?.addMessage(petReply)
                emit(Result.Success(petReply))
            } else {
                emit(Result.Error(Exception(response.message)))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
    


    companion object {
        @Volatile
        private var instance: ChatRepository? = null

        fun getInstance(context: Context? = null): ChatRepository {
            return instance ?: synchronized(this) {
                instance ?: ChatRepository(
                    chatDataManager = context?.let { ChatDataManager.getInstance(it) }
                ).also { instance = it }
            }
        }
    }
}
