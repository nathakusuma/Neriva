package com.nathakusuma.neriva.data.model

/**
 * Data model for chat API response wrapper
 */
data class ChatResponse(
    val success: Boolean,
    val message: String,
    val data: List<ChatMessage>
)

/**
 * Data model for send message request
 */
data class SendMessageRequest(
    val message: String
)

/**
 * Data model for single chat message response
 */
data class SendMessageResponse(
    val success: Boolean,
    val message: String,
    val data: ChatMessage
)

