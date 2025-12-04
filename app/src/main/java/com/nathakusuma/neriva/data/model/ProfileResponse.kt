package com.nathakusuma.neriva.data.model

import com.google.gson.annotations.SerializedName
import java.util.UUID

/**
 * Response model for profile operations
 */
data class ProfileResponse(
    val success: Boolean,
    val message: String,
    val data: ProfileData
)

/**
 * Profile data containing user and pet information
 */
data class ProfileData(
    val id: UUID,
    val name: String,
    val email: String,
    @SerializedName("profilePhoto")
    val profilePhoto: String?,
    val pet: Pet?
)

/**
 * Request model for updating profile
 * Note: API accepts name only in the profile field
 */
data class UpdateProfileRequest(
    val name: String
)
