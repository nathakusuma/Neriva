package com.nathakusuma.neriva.data.remote

import com.nathakusuma.neriva.data.model.ChatResponse
import com.nathakusuma.neriva.data.model.SendMessageRequest
import com.nathakusuma.neriva.data.model.SendMessageResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Retrofit API Service for Chat endpoints
 */
interface ChatApiService {

    /**
     * Get paginated chat history (newest first)
     * GET /api/chat?page=0&size=20
     *
     * @param page Page number (0-indexed)
     * @param size Number of messages per page
     * @return Chat history response
     */
    @GET("chat")
    suspend fun getChatHistory(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ChatResponse

    /**
     * Send a message to the pet
     * POST /api/chat
     *
     * @param request Message to send
     * @return Sent message response
     */
    @POST("chat")
    suspend fun sendMessage(
        @Body request: SendMessageRequest
    ): SendMessageResponse
}
