package com.nathakusuma.neriva.ui.profile

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nathakusuma.neriva.data.model.Result
import com.nathakusuma.neriva.data.repository.AuthRepository
import com.nathakusuma.neriva.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI State for Edit Profile Screen
 */
data class EditProfileUiState(
    val name: String = "",
    val email: String = "",
    val profilePhoto: String? = null,
    val selectedPhotoUri: Uri? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

/**
 * ViewModel for Edit Profile Screen
 * Manages UI state and handles profile update business logic
 */
class EditProfileViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val userRepository: UserRepository = UserRepository.getInstance(application.applicationContext)
    private val authRepository: AuthRepository = AuthRepository.getInstance()

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    /**
     * Load current user profile
     */
    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            userRepository.getUserProfile().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        // Already set loading state above
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            name = result.data.name,
                            email = result.data.email,
                            profilePhoto = result.data.profilePhoto,
                            isLoading = false
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = result.exception.message ?: "Failed to load profile",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    /**
     * Update name in UI state
     */
    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    /**
     * Update email in UI state
     */
    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    /**
     * Update selected photo URI
     */
    fun updatePhotoUri(uri: Uri?) {
        _uiState.value = _uiState.value.copy(selectedPhotoUri = uri)
    }

    /**
     * Save profile changes
     */
    fun saveProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            userRepository.updateUserProfile(
                name = _uiState.value.name,
                photoUri = _uiState.value.selectedPhotoUri
            ).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        // Already set loading state above
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            successMessage = "Profile updated successfully",
                            profilePhoto = result.data.profilePhoto,
                            selectedPhotoUri = null // Clear selected URI after successful upload
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = result.exception.message ?: "Failed to update profile",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    /**
     * Clear error and success messages
     */
    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }

    /**
     * Log out the current user
     */
    fun logout() {
        authRepository.logout()
    }
}
