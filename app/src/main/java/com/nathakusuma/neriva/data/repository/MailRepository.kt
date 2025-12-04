package com.nathakusuma.neriva.data.repository

import com.nathakusuma.neriva.data.local.TokenManager
import com.nathakusuma.neriva.data.model.Mail
import com.nathakusuma.neriva.data.model.Result
import com.nathakusuma.neriva.data.remote.MailApiService
import com.nathakusuma.neriva.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

/**
 * Repository for mail operations
 * Handles data operations and acts as a single source of truth
 */
class MailRepository(
    private val mailApiService: MailApiService = RetrofitClient.mailApiService,
    private val tokenManager: TokenManager = TokenManager.getInstance()
) {

    /**
     * Get all mails for current user
     * Returns a Flow of Result states (Loading, Success, Error)
     */
    fun getAllMails(): Flow<Result<List<Mail>>> = flow {
        try {
            emit(Result.Loading)

            val token = tokenManager.getToken()
            if (token.isNullOrEmpty()) {
                throw Exception("No authentication token found")
            }

            val response = mailApiService.getAllMails()

            if (response.success) {
                val mails = response.data ?: emptyList()
                emit(Result.Success(mails))
            } else {
                throw Exception(response.message)
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    /**
     * Mark a mail as read
     * Returns a Flow of Result states (Loading, Success, Error)
     *
     * @param mailId The ID of the mail to mark as read
     */
    fun markMailAsRead(mailId: UUID): Flow<Result<Boolean>> = flow {
        try {
            emit(Result.Loading)

            val token = tokenManager.getToken()
            if (token.isNullOrEmpty()) {
                throw Exception("No authentication token found")
            }

            val response = mailApiService.markMailAsRead(mailId)

            if (response.success) {
                emit(Result.Success(true))
            } else {
                throw Exception(response.message)
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    companion object {
        @Volatile
        private var instance: MailRepository? = null

        fun getInstance(): MailRepository {
            return instance ?: synchronized(this) {
                instance ?: MailRepository().also { instance = it }
            }
        }
    }
}

