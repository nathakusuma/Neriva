package com.nathakusuma.neriva.data.repository

import com.nathakusuma.neriva.data.model.Pet
import com.nathakusuma.neriva.data.model.Result
import com.nathakusuma.neriva.data.model.User
import com.nathakusuma.neriva.data.remote.MockUserApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repository for user-related operations (profile, pets, etc.)
 * Handles data operations and acts as a single source of truth
 */
class UserRepository(
    private val apiService: MockUserApiService = MockUserApiService.getInstance()
) {

    /**
     * Get user profile information
     * Returns a Flow of Result states (Loading, Success, Error)
     */
    fun getUserProfile(): Flow<Result<User>> = flow {
        try {
            emit(Result.Loading)
            val profile = apiService.getUserProfile()
            emit(Result.Success(profile))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    /**
     * Get user's pet information
     * Returns a Flow of Result states (Loading, Success, Error)
     */
    fun getUserPet(): Flow<Result<Pet>> = flow {
        try {
            emit(Result.Loading)
            val pet = apiService.getUserPet()
            emit(Result.Success(pet))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    /**
     * Update user profile information
     * Returns a Flow of Result states (Loading, Success, Error)
     */
    fun updateUserProfile(
        name: String,
        email: String,
        avatarUrl: String?
    ): Flow<Result<User>> = flow {
        try {
            emit(Result.Loading)
            val updatedUser = apiService.updateUserProfile(name, email, avatarUrl)
            emit(Result.Success(updatedUser))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(): UserRepository {
            return instance ?: synchronized(this) {
                instance ?: UserRepository().also { instance = it }
            }
        }
    }
}

