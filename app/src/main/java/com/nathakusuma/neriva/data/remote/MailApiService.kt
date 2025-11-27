package com.nathakusuma.neriva.data.remote

import com.nathakusuma.neriva.data.model.MarkAsReadResponse
import com.nathakusuma.neriva.data.model.MailResponse
import retrofit2.http.*

/**
 * Retrofit API Service for mail endpoints
 */
interface MailApiService {

    /**
     * Get all mails for current user
     * GET /api/notifications
     */
    @GET("notifications")
    suspend fun getAllMails(): MailResponse

    /**
     * Mark a specific mail as read
     * PUT /api/notifications/{id}/read
     *
     * @param mailId The ID of the mail to mark as read
     */
    @PUT("notifications/{id}/read")
    suspend fun markMailAsRead(
        @Path("id") mailId: Int
    ): MarkAsReadResponse
}

