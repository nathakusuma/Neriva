package com.nathakusuma.neriva.data.repository

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.nathakusuma.neriva.data.local.TokenManager
import com.nathakusuma.neriva.data.local.UserDataManager
import com.nathakusuma.neriva.data.model.Pet
import com.nathakusuma.neriva.data.model.Result
import com.nathakusuma.neriva.data.model.UpdateProfileRequest
import com.nathakusuma.neriva.data.model.User
import com.nathakusuma.neriva.data.remote.ProfileApiService
import com.nathakusuma.neriva.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

/**
 * Repository for user-related operations (profile, pets, etc.)
 * Handles data operations and acts as a single source of truth
 */
class UserRepository(
    private val profileApiService: ProfileApiService = RetrofitClient.profileApiService,
    private val userDataManager: UserDataManager = UserDataManager.getInstance(),
    private val tokenManager: TokenManager = TokenManager.getInstance(),
    private val context: Context? = null
) {

    /**
     * Get user profile information
     * Fetches from API with authentication and stores locally
     * Returns a Flow of Result states (Loading, Success, Error)
     */
    fun getUserProfile(): Flow<Result<User>> = flow {
        try {
            emit(Result.Loading)

            // Try to get from local storage first
            val localUser = userDataManager.getUser()
            if (localUser != null) {
                emit(Result.Success(localUser))
            }

            val token = tokenManager.getToken()
            if (token.isNullOrEmpty()) {
                throw Exception("No authentication token found")
            }

            val response = profileApiService.getUserProfile("Bearer $token")

            if (response.success) {
                // Convert ProfileData to User
                val user = User(
                    id = response.data.id,
                    name = response.data.name,
                    email = response.data.email,
                    profilePhoto = response.data.profilePhoto
                )

                // Save user and pet to local storage
                userDataManager.saveUser(user)
                response.data.pet?.let { userDataManager.savePet(it) }

                emit(Result.Success(user))
            } else {
                throw Exception(response.message)
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    /**
     * Observe user profile from local storage
     * Returns a Flow that emits whenever user data changes locally
     */
    fun observeUserProfile(): Flow<User?> = userDataManager.getUserFlow()

    /**
     * Observe pet from local storage
     * Returns a Flow that emits whenever pet data changes locally
     */
    fun observePet(): Flow<Pet?> = userDataManager.getPetFlow()

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
                // Fetch from API
                val token = tokenManager.getToken()
                if (token.isNullOrEmpty()) {
                    throw Exception("No authentication token found")
                }

                val response = profileApiService.getUserProfile("Bearer $token")

                if (response.success && response.data.pet != null) {
                    userDataManager.savePet(response.data.pet)
                    emit(Result.Success(response.data.pet))
                } else {
                    throw Exception("No pet found")
                }
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    /**
     * Update user profile information
     * Returns a Flow of Result states (Loading, Success, Error)
     *
     * @param name User's name to update
     * @param photoUri Optional URI of photo to upload
     */
    fun updateUserProfile(
        name: String,
        photoUri: Uri? = null
    ): Flow<Result<User>> = flow {
        try {
            emit(Result.Loading)

            val token = tokenManager.getToken()
            if (token.isNullOrEmpty()) {
                throw Exception("No authentication token found")
            }

            // Create profile JSON (name only as per API spec)
            val profileRequest = UpdateProfileRequest(name)
            val profileJson = Gson().toJson(profileRequest)
            val profileBody = profileJson.toRequestBody("application/json".toMediaTypeOrNull())

            // Create photo part if URI is provided
            val photoPart = photoUri?.let { uri ->
                context?.let { ctx ->
                    // Convert URI to File
                    val inputStream = ctx.contentResolver.openInputStream(uri)
                    val file = File(ctx.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
                    inputStream?.use { input ->
                        FileOutputStream(file).use { output ->
                            input.copyTo(output)
                        }
                    }

                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("photo", file.name, requestFile)
                }
            }

            val response = profileApiService.updateProfile(
                token = "Bearer $token",
                profile = profileBody,
                photo = photoPart
            )

            if (response.success) {
                val user = User(
                    id = response.data.id,
                    name = response.data.name,
                    email = response.data.email,
                    profilePhoto = response.data.profilePhoto
                )

                // Save updated user to local storage
                userDataManager.saveUser(user)
                response.data.pet?.let { userDataManager.savePet(it) }

                emit(Result.Success(user))
            } else {
                throw Exception(response.message)
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(context: Context? = null): UserRepository {
            return instance ?: synchronized(this) {
                instance ?: UserRepository(context = context).also { instance = it }
            }
        }
    }
}

