package com.nathakusuma.neriva.data.repository

import com.nathakusuma.neriva.data.model.ChatMessage
import com.nathakusuma.neriva.data.model.Result
import com.nathakusuma.neriva.data.remote.MockChatApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repository for chat-related operations
 * Handles data operations and acts as a single source of truth
 */
class ChatRepository(
    private val apiService: MockChatApiService = MockChatApiService.getInstance()
) {

    /**
     * Get chat messages with pet
     * Returns a Flow of Result states (Loading, Success, Error)
     */
    fun getChatMessages(): Flow<Result<List<ChatMessage>>> = flow {
        try {
            emit(Result.Loading)
            val messages = apiService.getChatMessages()
            emit(Result.Success(messages))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    /**
     * Send a message to pet
     * Returns a Flow of Result states (Loading, Success, Error)
     */
    fun sendMessage(message: String): Flow<Result<ChatMessage>> = flow {
        try {
            emit(Result.Loading)
            val sentMessage = apiService.sendMessage(message)
            emit(Result.Success(sentMessage))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    companion object {
        @Volatile
        private var instance: ChatRepository? = null

        fun getInstance(): ChatRepository {
            return instance ?: synchronized(this) {
                instance ?: ChatRepository().also { instance = it }
            }
        }
    }
}

