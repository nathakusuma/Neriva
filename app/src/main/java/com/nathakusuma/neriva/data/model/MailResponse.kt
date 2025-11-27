package com.nathakusuma.neriva.data.model

/**
 * Response model for mail operations
 */
data class MailResponse(
    val success: Boolean,
    val message: String,
    val data: List<Mail>?
)

/**
 * Response model for marking mail as read
 */
data class MarkAsReadResponse(
    val success: Boolean,
    val message: String,
    val data: Any?
)

