package com.nathakusuma.neriva.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data class representing a mail
 *
 * @property id Unique identifier for the mail
 * @property title The title/subject of the mail
 * @property content The full content of the mail
 * @property type Type of mail (HEALTH_TIP, REMINDER, VACCINE, etc.)
 * @property isRead Whether the mail has been read
 * @property createdAt Timestamp when the mail was created
 */
data class Mail(
    val id: Int,
    val title: String,
    val content: String,
    val type: String,
    @SerializedName("isRead")
    val isRead: Boolean = false,
    val createdAt: String
) {
    /**
     * Preview text for mail list (first 100 chars of content)
     */
    val preview: String
        get() = if (content.length > 100) {
            content.take(100) + "..."
        } else {
            content
        }

    /**
     * Body text for mail detail (same as content)
     */
    val body: String
        get() = content
}

