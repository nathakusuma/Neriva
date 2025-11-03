package com.nathakusuma.neriva.data.model

/**
 * Data class representing a mail message
 *
 * @property id Unique identifier for the mail
 * @property title The title/subject of the mail
 * @property preview A short preview of the mail content
 * @property body The full content of the mail
 */
data class Mail(
    val id: Int,
    val title: String,
    val preview: String,
    val body: String
)

