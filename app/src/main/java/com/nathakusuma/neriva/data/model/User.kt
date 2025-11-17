package com.nathakusuma.neriva.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data model representing a user
 */
data class User(
    val id: Int,
    val name: String,
    val email: String,
    @SerializedName("profilePhoto")
    val profilePhoto: String? = null,
    val unreadNotifications: Int = 0 // TODO: add this in the backend response
)
