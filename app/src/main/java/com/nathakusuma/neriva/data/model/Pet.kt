package com.nathakusuma.neriva.data.model

/**
 * Data model representing a pet
 */
data class Pet(
    val id: String,
    val name: String,
    val breed: String,
    val sex: String,
    val age: String,
    val weight: String,
    val isVaccinated: Boolean,
    val description: String,
    val imageUrl: String
)
