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
    suspend fun getUserProfile(): ProfileResponse

    /**
     * Update user profile (with or without photo)
     * PUT /api/profile
     *
     * @param profile JSON profile data as MultipartBody.Part
     * @param photo Optional profile photo file
     */
    @Multipart
    @PUT("profile")
    suspend fun updateProfile(
        @Part profile: MultipartBody.Part,
        @Part photo: MultipartBody.Part?
    ): ProfileResponse
}

