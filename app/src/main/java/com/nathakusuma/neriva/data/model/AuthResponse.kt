package com.nathakusuma.neriva.data.model

/**
 * Data model for authentication API response wrapper
 */
data class AuthResponse(
    val success: Boolean,
    val message: String,
    val data: AuthData?
)

/**
 * Data model for authentication response data containing JWT token, user info, and pet info
 */
data class AuthData(
    val token: String,
    val type: String,
    val user: User,
    val pet: Pet
)
