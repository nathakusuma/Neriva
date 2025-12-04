package com.nathakusuma.neriva.data.model

import com.google.gson.annotations.SerializedName
import java.util.UUID

/**
 * Data model representing a user
 */
data class User(
    val id: UUID,
    val name: String,
    val email: String,
    @SerializedName("profilePhoto")
    val profilePhoto: String? = null,
    val unreadMails: Int = 0 // TODO: add this in the backend response
)
