package com.nathakusuma.neriva.ui.mail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nathakusuma.neriva.data.model.Mail
import com.nathakusuma.neriva.data.model.Result
import com.nathakusuma.neriva.data.repository.MailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State for Mail Screen
 *
 * @property mails List of mail messages
 * @property isLoading Whether data is being loaded
 * @property errorMessage Error message to display, if any
 */
data class MailUiState(
    val mails: List<Mail> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel for managing mail screen state and business logic
 */
class MailViewModel(
    private val mailRepository: MailRepository = MailRepository.getInstance()
) : ViewModel() {
    private val _uiState = MutableStateFlow(MailUiState())
    val uiState: StateFlow<MailUiState> = _uiState.asStateFlow()

    init {
        loadMails()
    }

    /**
     * Load mail messages from API
     */
    private fun loadMails() {
        viewModelScope.launch {
            mailRepository.getAllMails().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                mails = result.data,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.exception.message ?: "Unknown error occurred"
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Delete a mail message (mark as read and remove from list)
     *
     * @param mailId The ID of the mail to delete
     */
    fun deleteMail(mailId: Int) {
        viewModelScope.launch {
            // Mark as read on the server
            mailRepository.markMailAsRead(mailId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        // Remove from local list
                        _uiState.update { currentState ->
                            currentState.copy(
                                mails = currentState.mails.filter { it.id != mailId }
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                errorMessage = "Failed to delete mail: ${result.exception.message}"
                            )
                        }
                    }
                    is Result.Loading -> {
                        // Optionally show loading state for delete operation
                    }
                }
            }
        }
    }

    /**
     * Refresh mail list
     */
    fun refreshMails() {
        loadMails()
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

