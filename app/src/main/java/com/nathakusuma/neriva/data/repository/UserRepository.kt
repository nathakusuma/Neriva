package com.nathakusuma.neriva.data.repository

import com.nathakusuma.neriva.data.local.UserDataManager
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
    private val apiService: MockUserApiService = MockUserApiService.getInstance(),
    private val userDataManager: UserDataManager = UserDataManager.getInstance()
) {

    /**
     * Get user profile information
     * First tries to get from local storage, falls back to API if not found
     * Returns a Flow of Result states (Loading, Success, Error)
     */
    fun getUserProfile(): Flow<Result<User>> = flow {
        try {
            emit(Result.Loading)

            // Try to get from local storage first
            val localUser = userDataManager.getUser()
            if (localUser != null) {
                emit(Result.Success(localUser))
            } else {
                // Fall back to API if no local data (for backward compatibility)
                val profile = apiService.getUserProfile()
                emit(Result.Success(profile))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    /**
     * Get user's pet information
     * First tries to get from local storage, falls back to API if not found
     * Returns a Flow of Result states (Loading, Success, Error)
     */
    fun getUserPet(): Flow<Result<Pet>> = flow {
        try {
            emit(Result.Loading)

            // Try to get from local storage first
            val localPet = userDataManager.getPet()
            if (localPet != null) {
                emit(Result.Success(localPet))
            } else {
                // Fall back to API if no local data (for backward compatibility)
                val pet = apiService.getUserPet()
                emit(Result.Success(pet))
            }
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
        profilePhoto: String?
    ): Flow<Result<User>> = flow {
        try {
            emit(Result.Loading)
            val updatedUser = apiService.updateUserProfile(name, email, profilePhoto)
            // Save updated user to local storage
            userDataManager.saveUser(updatedUser)
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

