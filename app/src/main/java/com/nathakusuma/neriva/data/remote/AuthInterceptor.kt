package com.nathakusuma.neriva.data.remote

import com.nathakusuma.neriva.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor to add Authorization header with Bearer token to requests
 */
class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Get token from TokenManager
        val token = TokenManager.getInstance().getToken()

        // If token exists, add Authorization header
        val newRequest = if (token != null) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}
