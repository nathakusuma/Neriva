package com.nathakusuma.neriva.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data model representing a pet
 */
data class Pet(
    val id: Int,
    val name: String,
    @SerializedName("animalType")
    val animalType: String,
    val gender: String,
    @SerializedName("birthDate")
    val birthDate: String,
    val weight: Double,
    val vaccine: Boolean,
    val description: String
)
