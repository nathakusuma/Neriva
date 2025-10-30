package com.nathakusuma.neriva.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nathakusuma.neriva.data.model.Result
import com.nathakusuma.neriva.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI State for Login Screen
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false
)

/**
 * ViewModel for Login Screen
 * Manages UI state and handles login business logic
 */
class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Update email field
     */
    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            errorMessage = null
        )
    }

    /**
     * Update password field
     */
    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            errorMessage = null
        )
    }

    /**
     * Toggle password visibility
     */
    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }

    /**
     * Perform login
     */
    fun login() {
        val currentState = _uiState.value

        // Validate inputs
        if (currentState.email.isBlank() || currentState.password.isBlank()) {
            _uiState.value = currentState.copy(
                errorMessage = "Email and password are required"
            )
            return
        }

        viewModelScope.launch {
            authRepository.login(currentState.email, currentState.password)
                .collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            _uiState.value = currentState.copy(
                                isLoading = true,
                                errorMessage = null
                            )
                        }
                        is Result.Success -> {
                            _uiState.value = currentState.copy(
                                isLoading = false,
                                isLoginSuccessful = true,
                                errorMessage = null
                            )
                        }
                        is Result.Error -> {
                            _uiState.value = currentState.copy(
                                isLoading = false,
                                errorMessage = result.exception.message ?: "Login failed"
                            )
                        }
                    }
                }
        }
    }

    /**
     * Reset login success state after navigation
     */
    fun resetLoginSuccess() {
        _uiState.value = _uiState.value.copy(isLoginSuccessful = false)
    }
}

