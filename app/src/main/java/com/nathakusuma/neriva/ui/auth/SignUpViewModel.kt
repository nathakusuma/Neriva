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
 * UI State for SignUp Screen
 */
data class SignUpUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSignUpSuccessful: Boolean = false
)

/**
 * ViewModel for SignUp Screen
 * Manages UI state and handles sign up business logic
 */
class SignUpViewModel(
    private val authRepository: AuthRepository = AuthRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    /**
     * Update name field
     */
    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(
            name = name,
            errorMessage = null
        )
    }

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
     * Perform sign up
     */
    fun signUp() {
        val currentState = _uiState.value

        // Validate inputs
        if (currentState.name.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "Name is required")
            return
        }
        if (currentState.email.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "Email is required")
            return
        }
        if (!currentState.email.contains("@") || !currentState.email.contains(".")) {
            _uiState.value = currentState.copy(errorMessage = "Enter a valid email")
            return
        }
        if (currentState.password.length < 6) {
            _uiState.value = currentState.copy(
                errorMessage = "Password must be at least 6 characters"
            )
            return
        }

        viewModelScope.launch {
            authRepository.signUp(currentState.name, currentState.email, currentState.password)
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
                                isSignUpSuccessful = true,
                                errorMessage = null
                            )
                        }
                        is Result.Error -> {
                            _uiState.value = currentState.copy(
                                isLoading = false,
                                errorMessage = result.exception.message ?: "Sign up failed"
                            )
                        }
                    }
                }
        }
    }

    /**
     * Reset sign up success state after navigation
     */
    fun resetSignUpSuccess() {
        _uiState.value = _uiState.value.copy(isSignUpSuccessful = false)
    }
}

