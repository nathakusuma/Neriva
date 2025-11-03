package com.nathakusuma.neriva.data.remote

import com.nathakusuma.neriva.data.model.ChatMessage
import kotlinx.coroutines.delay

/**
 * Mock API Service for Chat-related endpoints
 * TODO: Replace with actual Retrofit implementation
 */
class MockChatApiService {

    /**
     * Mock API call to get chat messages with pet
     * Simulates network delay
     */
    suspend fun getChatMessages(): List<ChatMessage> {
        delay(800)

        return listOf(
            ChatMessage(
                id = 1,
                fromMe = true,
                author = "Joshua",
                text = "Hello! Alex, I'm Joshua",
                time = "09:25 AM"
            ),
            ChatMessage(
                id = 2,
                fromMe = false,
                author = "Alex",
                text = "Hello ! Joshua, How are you?",
                time = "09:25 AM"
            ),
            ChatMessage(
                id = 3,
                fromMe = true,
                author = "Joshua",
                text = "You did your job well!",
                time = "09:25 AM"
            ),
            ChatMessage(
                id = 4,
                fromMe = false,
                author = "Alex",
                text = "Have a great working week!!",
                time = "09:25 AM"
            ),
            ChatMessage(
                id = 5,
                fromMe = false,
                author = "Alex",
                text = "Hope you like it",
                time = "09:25 AM"
            ),
            ChatMessage(
                id = 6,
                fromMe = true,
                author = "Joshua",
                text = "Thank you Jhon!",
                time = "09:25 AM"
            )
        )
    }

    /**
     * Mock API call to send a message
     * Simulates network delay
     */
    suspend fun sendMessage(message: String): ChatMessage {
        delay(500)

        return ChatMessage(
            id = System.currentTimeMillis().toInt(),
            fromMe = true,
            author = "Joshua",
            text = message,
            time = "09:26 AM"
        )
    }

    companion object {
        @Volatile
        private var instance: MockChatApiService? = null

        fun getInstance(): MockChatApiService {
            return instance ?: synchronized(this) {
                instance ?: MockChatApiService().also { instance = it }
            }
        }
    }
}
