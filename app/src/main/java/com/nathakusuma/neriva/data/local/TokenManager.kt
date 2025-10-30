package com.nathakusuma.neriva.data.local

/**
 * Mock Token Manager for JWT token storage
 * TODO: Replace with actual DataStore implementation
 */
class TokenManager {
    private var token: String? = null

    /**
     * Save JWT token
     */
    fun saveToken(token: String) {
        this.token = token
        println("TokenManager: Token saved: $token")
    }

    /**
     * Get stored JWT token
     */
    fun getToken(): String? {
        return token
    }

    /**
     * Clear stored JWT token
     */
    fun clearToken() {
        token = null
        println("TokenManager: Token cleared")
    }

    /**
     * Check if user is authenticated (has valid token)
     */
    fun isAuthenticated(): Boolean {
        return token != null
    }

    companion object {
        @Volatile
        private var instance: TokenManager? = null

        fun getInstance(): TokenManager {
            return instance ?: synchronized(this) {
                instance ?: TokenManager().also { instance = it }
            }
        }
    }
}

