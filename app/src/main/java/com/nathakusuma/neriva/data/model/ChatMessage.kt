package com.nathakusuma.neriva.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data model representing a chat message from the API
 */
data class ChatMessage(
    val id: Int,
    val message: String,
    @SerializedName("senderType")
    val senderType: SenderType,
    @SerializedName("createdAt")
    val createdAt: String
) {
    /**
     * Check if message is from the user
     */
    val fromMe: Boolean
        get() = senderType == SenderType.USER
}

/**
 * Enum for message sender type
 */
enum class SenderType {
    @SerializedName("USER")
    USER,
    @SerializedName("PET")
    PET
}
