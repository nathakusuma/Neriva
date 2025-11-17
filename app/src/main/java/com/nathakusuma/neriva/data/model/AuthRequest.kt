package com.nathakusuma.neriva.data.model

/**
 * Request model for user registration
 */
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

/**
 * Request model for user login
 */
data class LoginRequest(
    val email: String,
    val password: String
)

