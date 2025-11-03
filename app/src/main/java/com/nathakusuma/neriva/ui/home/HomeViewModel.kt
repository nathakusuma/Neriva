package com.nathakusuma.neriva.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nathakusuma.neriva.data.model.Pet
import com.nathakusuma.neriva.data.model.Result
import com.nathakusuma.neriva.data.model.User
import com.nathakusuma.neriva.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI State for Home Screen
 */
data class HomeUiState(
    val userProfile: User? = null,
    val pet: Pet? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel for Home Screen
 * Manages UI state and handles home screen business logic
 */
class HomeViewModel(
    private val userRepository: UserRepository = UserRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    /**
     * Load all home screen data (user profile and pet info)
     */
    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            // Load user profile
            userRepository.getUserProfile().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        // Already set loading state above
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            userProfile = result.data
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = result.exception.message ?: "Failed to load profile"
                        )
                    }
                }
            }

            // Load pet information
            userRepository.getUserPet().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        // Already set loading state above
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            pet = result.data,
                            isLoading = false
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = result.exception.message ?: "Failed to load pet data",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
