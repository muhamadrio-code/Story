package com.riopermana.story.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore


val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object DataStoreUtil {

    suspend inline fun saveToken(context: Context, newToken: String) {
        context.sessionDataStore.edit { preferences ->
            preferences[PreferencesKeys.TOKEN_KEY] = newToken
        }
    }

    suspend inline fun clearSession(context: Context) {
        context.sessionDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend inline fun getCurrentSession(context: Context, crossinline action: (String?) -> Unit) {
        context.sessionDataStore.data.collect { preferences ->
            val mToken = preferences[PreferencesKeys.TOKEN_KEY]
            action(mToken)
        }
    }

    suspend inline fun <T> savePrefSettings(
        context: Context,
        key: Preferences.Key<T>,
        value: T
    ) {
        context.settingsDataStore.edit { preferences ->
            preferences[key] = value
        }
    }
}
