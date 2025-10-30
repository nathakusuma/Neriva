package com.nathakusuma.neriva.data.model

/**
 * Data model for authentication response containing JWT token and user info
 */
data class AuthResponse(
    val token: String,
    val user: User
)

