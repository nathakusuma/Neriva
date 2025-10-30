package com.nathakusuma.neriva.ui.onboarding

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * UI State for Onboarding Screen
 */
data class OnboardingUiState(
    val currentPageIndex: Int = 0,
    val totalPages: Int = OnboardingData.pages.size
) {
    val isFirstPage: Boolean get() = currentPageIndex == 0
    val isLastPage: Boolean get() = currentPageIndex == totalPages - 1
}

/**
 * ViewModel for Onboarding Screen
 * Manages UI state and handles onboarding navigation logic
 */
class OnboardingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    /**
     * Navigate to the next page
     */
    fun navigateNext() {
        val currentState = _uiState.value
        if (!currentState.isLastPage) {
            _uiState.value = currentState.copy(
                currentPageIndex = currentState.currentPageIndex + 1
            )
        }
    }

    /**
     * Navigate to the previous page
     */
    fun navigateBack() {
        val currentState = _uiState.value
        if (!currentState.isFirstPage) {
            _uiState.value = currentState.copy(
                currentPageIndex = currentState.currentPageIndex - 1
            )
        }
    }

    /**
     * Reset to initial page (useful for testing or restarting onboarding)
     */
    fun reset() {
        _uiState.value = OnboardingUiState()
    }
}

