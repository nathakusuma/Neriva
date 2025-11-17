package com.nathakusuma.neriva.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * Token Manager for JWT token storage using DataStore
 */
class TokenManager private constructor(context: Context) {

    private val appContext: Context = context.applicationContext

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

    companion object {
        @Volatile
        private var instance: TokenManager? = null

        private val TOKEN_KEY = stringPreferencesKey("auth_token")

        fun getInstance(context: Context? = null): TokenManager {
            return instance ?: synchronized(this) {
                if (instance == null && context == null) {
                    throw IllegalStateException("TokenManager must be initialized with context first")
                }
                instance ?: TokenManager(context!!).also { instance = it }
            }
        }
    }

    /**
     * Save JWT token
     */
    fun saveToken(token: String) {
        runBlocking {
            appContext.dataStore.edit { preferences ->
                preferences[TOKEN_KEY] = token
            }
            println("TokenManager: Token saved: $token")
        }
    }

    /**
     * Get stored JWT token
     */
    fun getToken(): String? {
        return runBlocking {
            appContext.dataStore.data.map { preferences ->
                preferences[TOKEN_KEY]
            }.first()
        }
    }

    /**
     * Get token as Flow
     */
    fun getTokenFlow(): Flow<String?> {
        return appContext.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }
    }

    /**
     * Clear stored JWT token
     */
    fun clearToken() {
        runBlocking {
            appContext.dataStore.edit { preferences ->
                preferences.remove(TOKEN_KEY)
            }
            println("TokenManager: Token cleared")
        }
    }

    /**
     * Check if user is authenticated (has valid token)
     */
    fun isAuthenticated(): Boolean {
        return getToken() != null
    }
}

