package com.farrelfeno.substoryappintermediate.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("token")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    fun getToken(): Flow<String> {
        return dataStore.data.map {
            it[token] ?: "null"
        }
    }
    private val token = stringPreferencesKey("token")
    private val firstLogin = booleanPreferencesKey("first_login")
    fun isFirstTime(): Flow<Boolean> {
        return dataStore.data.map {
            it[firstLogin] ?: true
        }
    }
    suspend fun saveToken(token: String) {
        dataStore.edit {
            it[this.token] = token
        }
    }
    suspend fun setSession(first: Boolean) {
        dataStore.edit {
            it[this.firstLogin] = first
        }
    }
    suspend fun clearToken() {
        dataStore.edit {
            it.clear()
        }
    }
    companion object {
        @Volatile
        private var instance: UserPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference =
            instance ?: synchronized(this) {
                instance ?: UserPreference(dataStore)
            }.also { instance = it }
    }
}