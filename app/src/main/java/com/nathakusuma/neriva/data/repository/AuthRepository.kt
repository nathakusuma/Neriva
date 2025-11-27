package com.nathakusuma.neriva.data.repository

import android.content.Context
import com.google.gson.Gson
import com.nathakusuma.neriva.data.local.TokenManager
import com.nathakusuma.neriva.data.local.UserDataManager
import com.nathakusuma.neriva.data.model.AuthResponse
import com.nathakusuma.neriva.data.model.LoginRequest
import com.nathakusuma.neriva.data.model.RegisterRequest
import com.nathakusuma.neriva.data.model.Result
import com.nathakusuma.neriva.data.remote.AuthApiService
import com.nathakusuma.neriva.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

/**
 * Repository for authentication operations
 * Handles data operations and acts as a single source of truth
 */
class AuthRepository(
    private val apiService: AuthApiService = RetrofitClient.authApiService,
    private val tokenManager: TokenManager = TokenManager.getInstance(),
    private val userDataManager: UserDataManager = UserDataManager.getInstance(),
    @Suppress("unused") private val context: Context? = null
) {

    /**
     * Parse error message from HTTP exception response
     */
    private fun parseErrorMessage(exception: HttpException): String {
        return try {
            val errorBody = exception.response()?.errorBody()?.string()
            if (errorBody != null) {
                val errorResponse = Gson().fromJson(errorBody, AuthResponse::class.java)
                errorResponse.message
            } else {
                "HTTP ${exception.code()}: ${exception.message()}"
            }
        } catch (e: Exception) {
            "HTTP ${exception.code()}: ${exception.message()}"
        }
    }

    /**
     * Login user with email and password
     * Returns a Flow of Result states (Loading, Success, Error)
     */
    fun login(email: String, password: String): Flow<Result<AuthResponse>> = flow {
        try {
            emit(Result.Loading)
            val request = LoginRequest(email, password)
            val response = apiService.login(request)

            if (response.success && response.data != null) {
                // Save token
                tokenManager.saveToken(response.data.token)
                // Save user and pet data
                userDataManager.saveUser(response.data.user)
                userDataManager.savePet(response.data.pet)
                emit(Result.Success(response))
            } else {
                emit(Result.Error(Exception(response.message)))
            }
        } catch (e: HttpException) {
            val errorMessage = parseErrorMessage(e)
            emit(Result.Error(Exception(errorMessage)))
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
            val request = RegisterRequest(name, email, password)
            val response = apiService.register(request)

            if (response.success && response.data != null) {
                // Save token
                tokenManager.saveToken(response.data.token)
                // Save user and pet data
                userDataManager.saveUser(response.data.user)
                userDataManager.savePet(response.data.pet)
                emit(Result.Success(response))
            } else {
                emit(Result.Error(Exception(response.message)))
            }
        } catch (e: HttpException) {
            val errorMessage = parseErrorMessage(e)
            emit(Result.Error(Exception(errorMessage)))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    /**
     * Logout user and clear token, user data, and pet data
     */
    fun logout() {
        tokenManager.clearToken()
        userDataManager.clearAll()
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

        fun getInstance(context: Context? = null): AuthRepository {
            return instance ?: synchronized(this) {
                instance ?: AuthRepository(context = context).also { instance = it }
            }
        }
    }
}
