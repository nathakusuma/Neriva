package com.nathakusuma.neriva.data.remote

import com.nathakusuma.neriva.data.model.ProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Retrofit API Service for Profile endpoints
 */
interface ProfileApiService {

    /**
     * Get user profile
     * GET /api/profile
     */
    @GET("profile")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): ProfileResponse

    /**
     * Update user profile (with or without photo)
     * PUT /api/profile
     *
     * @param token Authorization bearer token
     * @param profile JSON string containing profile data (name, email)
     * @param photo Optional profile photo file
     */
    @Multipart
    @PUT("profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Part("profile") profile: RequestBody,
        @Part photo: MultipartBody.Part? = null
    ): ProfileResponse
}

