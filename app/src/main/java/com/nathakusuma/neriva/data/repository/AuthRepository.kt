package com.nathakusuma.neriva.data.repository

import com.nathakusuma.neriva.data.local.TokenManager
import com.nathakusuma.neriva.data.model.AuthResponse
import com.nathakusuma.neriva.data.model.Result
import com.nathakusuma.neriva.data.remote.MockAuthApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repository for authentication operations
 * Handles data operations and acts as a single source of truth
 */
class AuthRepository(
    private val apiService: MockAuthApiService = MockAuthApiService.getInstance(),
    private val tokenManager: TokenManager = TokenManager.getInstance()
) {

    /**
     * Login user with email and password
     * Returns a Flow of Result states (Loading, Success, Error)
     */
    fun login(email: String, password: String): Flow<Result<AuthResponse>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.login(email, password)
            tokenManager.saveToken(response.token)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    /**
     * Sign up new user
     * Returns a Flow of Result states (Loading, Success, Error)
     */
    fun signUp(name: String, email: String, password: String): Flow<Result<AuthResponse>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.signUp(name, email, password)
            tokenManager.saveToken(response.token)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    /**
     * Logout user and clear token
     */
    fun logout() {
        tokenManager.clearToken()
    }

    /**
     * Check if user is authenticated
     */
    fun isAuthenticated(): Boolean {
        return tokenManager.isAuthenticated()
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(): AuthRepository {
            return instance ?: synchronized(this) {
                instance ?: AuthRepository().also { instance = it }
            }
        }
    }
}

