package com.nathakusuma.neriva.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nathakusuma.neriva.data.model.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Manager for storing and retrieving chat messages locally using DataStore
 */
class ChatDataManager private constructor(context: Context) {

    private val appContext: Context = context.applicationContext
    private val gson = Gson()

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "chat_data_prefs")

    companion object {
        @Volatile
        private var instance: ChatDataManager? = null

        private val CHAT_MESSAGES_KEY = stringPreferencesKey("chat_messages")
        private val LAST_MESSAGE_ID_KEY = intPreferencesKey("last_message_id")

        fun getInstance(context: Context? = null): ChatDataManager {
            return instance ?: synchronized(this) {
                if (instance == null && context == null) {
                    throw IllegalStateException("ChatDataManager must be initialized with context first")
                }
                instance ?: ChatDataManager(context!!).also { instance = it }
            }
        }
    }

    /**
     * Save chat messages
     */
    suspend fun saveMessages(messages: List<ChatMessage>) {
        appContext.dataStore.edit { preferences ->
            preferences[CHAT_MESSAGES_KEY] = gson.toJson(messages)
            if (messages.isNotEmpty()) {
                preferences[LAST_MESSAGE_ID_KEY] = messages.maxOf { it.id }
            }
        }
    }

    /**
     * Get stored chat messages
     */
    suspend fun getMessages(): List<ChatMessage> {
        return appContext.dataStore.data.map { preferences ->
            preferences[CHAT_MESSAGES_KEY]?.let { json ->
                val type = object : TypeToken<List<ChatMessage>>() {}.type
                gson.fromJson<List<ChatMessage>>(json, type)
            } ?: emptyList()
        }.first()
    }

    /**
     * Get chat messages as Flow
     */
    fun getMessagesFlow(): Flow<List<ChatMessage>> {
        return appContext.dataStore.data.map { preferences ->
            preferences[CHAT_MESSAGES_KEY]?.let { json ->
                val type = object : TypeToken<List<ChatMessage>>() {}.type
                gson.fromJson<List<ChatMessage>>(json, type)
            } ?: emptyList()
        }
    }

    /**
     * Add a new message to the stored messages
     */
    suspend fun addMessage(message: ChatMessage) {
        val currentMessages = getMessages().toMutableList()
        // Avoid duplicates
        if (!currentMessages.any { it.id == message.id }) {
            currentMessages.add(message)
            saveMessages(currentMessages.sortedBy { it.id })
        }
    }

    /**
     * Add multiple new messages to the stored messages
     */
    suspend fun addMessages(messages: List<ChatMessage>) {
        val currentMessages = getMessages().toMutableList()
        val currentIds = currentMessages.map { it.id }.toSet()
        val newMessages = messages.filter { it.id !in currentIds }
        if (newMessages.isNotEmpty()) {
            currentMessages.addAll(newMessages)
            saveMessages(currentMessages.sortedBy { it.id })
        }
    }
}
