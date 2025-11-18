package com.st10140587.prog7314_poe

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("prefs")

private object Keys {
    val USE_CELSIUS = booleanPreferencesKey("use_celsius")
    val LANGUAGE = stringPreferencesKey("language")   // "en", "fr", "af"
}

class SettingsStore(private val ctx: Context) {

    // Temperature units
    val useCelsius: Flow<Boolean> = ctx.dataStore.data
        .map { prefs -> prefs[Keys.USE_CELSIUS] ?: true }

    suspend fun setUseCelsius(value: Boolean) {
        ctx.dataStore.edit { prefs ->
            prefs[Keys.USE_CELSIUS] = value
        }
    }

    // Preferred language (for the selector)
    val preferredLanguage: Flow<String> = ctx.dataStore.data
        .map { prefs -> prefs[Keys.LANGUAGE] ?: "en" }

    suspend fun setPreferredLanguage(code: String) {
        ctx.dataStore.edit { prefs ->
            prefs[Keys.LANGUAGE] = code
        }
    }
}
