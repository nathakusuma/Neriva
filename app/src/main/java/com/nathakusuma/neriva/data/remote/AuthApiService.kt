package com.nathakusuma.neriva.data.remote

import com.nathakusuma.neriva.data.model.AuthResponse
import com.nathakusuma.neriva.data.model.LoginRequest
import com.nathakusuma.neriva.data.model.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit API Service for Authentication endpoints
 */
interface AuthApiService {

    /**
     * Register a new user
     * POST /api/auth/register
     */
    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): AuthResponse

    /**
     * Login user
     * POST /api/auth/login
     */
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): AuthResponse
}
