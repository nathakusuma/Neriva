package com.nathakusuma.neriva.data.model
/**
 * Data model representing a chat message
 */
data class ChatMessage(
    val id: Int,
    val fromMe: Boolean,
    val author: String,
    val text: String,
    val time: String
)
