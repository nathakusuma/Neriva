package com.nathakusuma.neriva.data.remote

import com.nathakusuma.neriva.data.model.Pet
import com.nathakusuma.neriva.data.model.User
import kotlinx.coroutines.delay

/**
 * Mock API Service for User-related endpoints (profile, pets, etc.)
 * TODO: Replace with actual Retrofit implementation
 */
class MockUserApiService {

    /**
     * Mock API call to get user profile
     * Simulates network delay
     */
    suspend fun getUserProfile(): User {
        delay(800)

        return User(
            id = System.currentTimeMillis().toInt(),
            name = "Josh",
            email = "josh@example.com",
            profilePhoto = "https://i.pravatar.cc/150?img=12",
            unreadNotifications = 3
        )
    }

    /**
     * Mock API call to get user's pet information
     * Simulates network delay
     */
    suspend fun getUserPet(): Pet {
        delay(1000)

        return Pet(
            id = System.currentTimeMillis().toInt(),
            name = "Alex",
            animalType = "Australian Shepherd dog",
            gender = "Male",
            birthDate = "2023-01-01",
            weight = 10.0,
            vaccine = true,
            description = "Lorem ipsum dolor sit amet consectetur. Sed turpis nullam scelerisque mi quam curabitur proin. Lacus purus dolo vitae et odio odio. Ornare scelerisque et feugiat curabitur in mauris quis etiam diam."
        )
    }

    /**
     * Mock API call to update user profile
     * Simulates network delay
     */
    suspend fun updateUserProfile(
        name: String,
        email: String,
        profilePhoto: String?
    ): User {
        delay(800)

        return User(
            id = System.currentTimeMillis().toInt(),
            name = name,
            email = email,
            profilePhoto = profilePhoto ?: "https://i.pravatar.cc/150?img=12",
            unreadNotifications = 3
        )
    }

    companion object {
        @Volatile
        private var instance: MockUserApiService? = null

        fun getInstance(): MockUserApiService {
            return instance ?: synchronized(this) {
                instance ?: MockUserApiService().also { instance = it }
            }
        }
    }
}
