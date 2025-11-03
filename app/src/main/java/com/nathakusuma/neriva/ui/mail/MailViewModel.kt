package com.nathakusuma.neriva.ui.mail

import androidx.lifecycle.ViewModel
import com.nathakusuma.neriva.data.model.Mail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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
class MailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MailUiState())
    val uiState: StateFlow<MailUiState> = _uiState.asStateFlow()

    init {
        loadMails()
    }

    /**
     * Load mail messages
     * TODO: Replace with REST API integration to load mail list from backend
     */
    private fun loadMails() {
        _uiState.update { it.copy(isLoading = true) }

        // Simulate loading with fake data for now
        val sampleMails = listOf(
            Mail(
                id = 1,
                title = "🌟 How was your day, Son?",
                preview = "Hey Son, how was your day today? I hope everything went well for you, and if anything was tough...",
                body = """
                    Hey Son, how was your day today?
                    I hope everything went well for you, and if anything was tough, remember I'm here for you.
                    Always proud of you, no matter what happens.
                    
                    Text me whenever you want, Son :)
                    
                    Love, Mom
                """.trimIndent()
            ),
            Mail(
                id = 2,
                title = "Gift from your Mom! Check it out!",
                preview = "I've got a little surprise for you, Joshua! A small gift just to brighten your day...",
                body = """
                    I've got a little surprise for you, Joshua!
                    A small gift just to brighten your day and remind you how loved you are.
                    
                    Love you,
                    Mom
                """.trimIndent()
            ),
            Mail(
                id = 3,
                title = "Good job, don't forget to take a rest...",
                preview = "Good job today, Joshua! I can see how hard you're working...",
                body = """
                    Good job today, Joshua!
                    I can see how hard you're working and I'm really proud.
                    
                    But don't forget to take a rest, okay?
                    Your health is important too.
                    
                    Love, Mom
                """.trimIndent()
            ),
            Mail(
                id = 4,
                title = "Love you, Son 💖",
                preview = "Joshua, I just want you to know how much I love you. No matter what, you'll always be my...",
                body = """
                    Joshua, I just want you to know how much I love you.
                    No matter what, you'll always be my little boy.
                    
                    Love you so much,
                    Mom
                """.trimIndent()
            ),
            Mail(
                id = 5,
                title = "Good luck for your exam, Josh 😊",
                preview = "Good luck for your exam, Josh! I know you've prepared well and you can do it...",
                body = """
                    Good luck for your exam, Josh!
                    I know you've prepared well and you can do it.
                    
                    Believe in yourself, and remember I'm cheering for you.
                    
                    Love, Mom
                """.trimIndent()
            )
        )

        _uiState.update {
            it.copy(
                mails = sampleMails,
                isLoading = false
            )
        }
    }

    /**
     * Delete a mail message
     * TODO: Integrate REST API call to delete / archive this mail
     *
     * @param mailId The ID of the mail to delete
     */
    fun deleteMail(mailId: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                mails = currentState.mails.filter { it.id != mailId }
            )
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

