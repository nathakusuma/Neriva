package com.nathakusuma.neriva.data.model

import com.google.gson.annotations.SerializedName
import java.util.UUID

/**
 * Data model representing a pet
 */
data class Pet(
    val id: UUID,
    val name: String,
    @SerializedName("animalType")
    val animalType: String,
    val gender: String,
    @SerializedName("birthDate")
    val birthDate: String,
    val weight: Double,
    val vaccine: Boolean,
    val description: String,
    @SerializedName("petHomeImage")
    val petHomeImage: String?,
    @SerializedName("petProfileImage")
    val petProfileImage: String?,
    @SerializedName("welcomingStatement")
    val welcomingStatement: String?
)
