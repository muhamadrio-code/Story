package com.riopermana.story.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore


val Context.sessionDataStore : DataStore<Preferences> by preferencesDataStore(name = "session")

object DataStoreUtil {

    suspend fun saveSession(context: Context, newToken: String) {
        context.sessionDataStore.edit { preferences ->
            preferences[PreferencesKeys.TOKEN_STRING] = newToken
        }
    }

    suspend fun clearSession(context: Context) {
        context.sessionDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
