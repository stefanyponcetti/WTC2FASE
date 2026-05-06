package com.example.wtcapp.data

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONObject

private val Context.sessionDataStore by preferencesDataStore(name = "session")

class SessionManager(private val context: Context) {
    private val tokenKey = stringPreferencesKey("jwt_token")
    private val userIdKey = stringPreferencesKey("user_id")

    suspend fun saveSession(token: String, userId: String) {
        context.sessionDataStore.edit { preferences ->
            preferences[tokenKey] = token
            preferences[userIdKey] = userId
        }
    }

    suspend fun getToken(): String? {
        return context.sessionDataStore.data
            .map { preferences -> preferences[tokenKey] }
            .first()
    }

    suspend fun getUserId(): String? {
        return context.sessionDataStore.data
            .map { preferences -> preferences[userIdKey] }
            .first()
    }

    suspend fun clearSession() {
        context.sessionDataStore.edit { preferences ->
            preferences.remove(tokenKey)
            preferences.remove(userIdKey)
        }
    }

    companion object {
        fun extractUserIdFromJwt(token: String): String? {
            return runCatching {
                val payload = token.split(".").getOrNull(1) ?: return null
                val normalizedPayload = payload.padEnd(payload.length + (4 - payload.length % 4) % 4, '=')
                val json = String(
                    Base64.decode(
                        normalizedPayload,
                        Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
                    )
                )
                JSONObject(json).optString("nameid").takeIf { it.isNotBlank() }
            }.getOrNull()
        }
    }
}
