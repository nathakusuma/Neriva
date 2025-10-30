package com.nathakusuma.neriva.data.remote

import com.nathakusuma.neriva.data.model.AuthResponse
import com.nathakusuma.neriva.data.model.User
import kotlinx.coroutines.delay

/**
 * Mock API Service to simulate REST API calls
 * TODO: Replace with actual Retrofit implementation
 */
class MockApiService {

    /**
     * Mock login API call
     * Simulates network delay and authentication
     */
    suspend fun login(email: String, password: String): AuthResponse {
        // Simulate network delay
        delay(1000)

        // Validate credentials
        if (!email.contains("@")) {
            throw Exception("Invalid email format")
        }
        if (password.length < 8) {
            throw Exception("Password must be at least 8 characters")
        }

        // Mock successful response
        return AuthResponse(
            token = "mock_jwt_token_${System.currentTimeMillis()}",
            user = User(
                id = "user_${System.currentTimeMillis()}",
                name = email.substringBefore("@"),
                email = email
            )
        )
    }

    /**
     * Mock sign up API call
     * Simulates network delay and registration
     */
    suspend fun signUp(name: String, email: String, password: String): AuthResponse {
        // Simulate network delay
        delay(1000)

        // Validate inputs
        if (name.isBlank()) {
            throw Exception("Name is required")
        }
        if (!email.contains("@") || !email.contains(".")) {
            throw Exception("Invalid email format")
        }
        if (password.length < 6) {
            throw Exception("Password must be at least 6 characters")
        }

        // Mock successful response
        return AuthResponse(
            token = "mock_jwt_token_${System.currentTimeMillis()}",
            user = User(
                id = "user_${System.currentTimeMillis()}",
                name = name,
                email = email
            )
        )
    }

    companion object {
        @Volatile
        private var instance: MockApiService? = null

        fun getInstance(): MockApiService {
            return instance ?: synchronized(this) {
                instance ?: MockApiService().also { instance = it }
            }
        }
    }
}

