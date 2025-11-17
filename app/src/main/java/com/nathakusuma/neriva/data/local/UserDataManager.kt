package com.nathakusuma.neriva.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.nathakusuma.neriva.data.model.Pet
import com.nathakusuma.neriva.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * Manager for storing and retrieving user and pet data locally using DataStore
 */
class UserDataManager private constructor(context: Context) {

    private val appContext: Context = context.applicationContext
    private val gson = Gson()

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data_prefs")

    companion object {
        @Volatile
        private var instance: UserDataManager? = null

        private val USER_DATA_KEY = stringPreferencesKey("user_data")
        private val PET_DATA_KEY = stringPreferencesKey("pet_data")

        fun getInstance(context: Context? = null): UserDataManager {
            return instance ?: synchronized(this) {
                if (instance == null && context == null) {
                    throw IllegalStateException("UserDataManager must be initialized with context first")
                }
                instance ?: UserDataManager(context!!).also { instance = it }
            }
        }
    }

    /**
     * Save user data
     */
    fun saveUser(user: User) {
        runBlocking {
            appContext.dataStore.edit { preferences ->
                preferences[USER_DATA_KEY] = gson.toJson(user)
            }
            println("UserDataManager: User saved: ${user.name}")
        }
    }

    /**
     * Get stored user data
     */
    fun getUser(): User? {
        return runBlocking {
            appContext.dataStore.data.map { preferences ->
                preferences[USER_DATA_KEY]?.let { json ->
                    gson.fromJson(json, User::class.java)
                }
            }.first()
        }
    }

    /**
     * Get user as Flow
     */
    fun getUserFlow(): Flow<User?> {
        return appContext.dataStore.data.map { preferences ->
            preferences[USER_DATA_KEY]?.let { json ->
                gson.fromJson(json, User::class.java)
            }
        }
    }

    /**
     * Save pet data
     */
    fun savePet(pet: Pet) {
        runBlocking {
            appContext.dataStore.edit { preferences ->
                preferences[PET_DATA_KEY] = gson.toJson(pet)
            }
            println("UserDataManager: Pet saved: ${pet.name}")
        }
    }

    /**
     * Get stored pet data
     */
    fun getPet(): Pet? {
        return runBlocking {
            appContext.dataStore.data.map { preferences ->
                preferences[PET_DATA_KEY]?.let { json ->
                    gson.fromJson(json, Pet::class.java)
                }
            }.first()
        }
    }

    /**
     * Get pet as Flow
     */
    fun getPetFlow(): Flow<Pet?> {
        return appContext.dataStore.data.map { preferences ->
            preferences[PET_DATA_KEY]?.let { json ->
                gson.fromJson(json, Pet::class.java)
            }
        }
    }

    /**
     * Clear all stored user and pet data
     */
    fun clearAll() {
        runBlocking {
            appContext.dataStore.edit { preferences ->
                preferences.remove(USER_DATA_KEY)
                preferences.remove(PET_DATA_KEY)
            }
            println("UserDataManager: All data cleared")
        }
    }
}

