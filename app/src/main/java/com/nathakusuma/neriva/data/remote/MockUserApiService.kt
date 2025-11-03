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
            id = "user_${System.currentTimeMillis()}",
            name = "Josh",
            email = "josh@example.com",
            avatarUrl = "https://i.pravatar.cc/150?img=12",
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
            id = "pet_${System.currentTimeMillis()}",
            name = "Alex",
            breed = "Australian Shepherd dog",
            sex = "Male",
            age = "1 Year",
            weight = "10kg",
            isVaccinated = true,
            description = "Lorem ipsum dolor sit amet consectetur. Sed turpis nullam scelerisque mi quam curabitur proin. Lacus purus dolo vitae et odio odio. Ornare scelerisque et feugiat curabitur in mauris quis etiam diam.",
            imageUrl = "https://images.unsplash.com/photo-1548199973-03cce0bbc87b?w=400"
        )
    }

    /**
     * Mock API call to update user profile
     * Simulates network delay
     */
    suspend fun updateUserProfile(
        name: String,
        email: String,
        avatarUrl: String?
    ): User {
        delay(800)

        return User(
            id = "user_${System.currentTimeMillis()}",
            name = name,
            email = email,
            avatarUrl = avatarUrl ?: "https://i.pravatar.cc/150?img=12",
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
