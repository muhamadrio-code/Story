package com.riopermana.story.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.riopermana.story.model.User


val Context.sessionDataStore : DataStore<Preferences> by preferencesDataStore(name = "session")

object DataStoreUtil {

    suspend fun saveToken(context: Context, newToken: String) {
        context.sessionDataStore.edit { preferences ->
            preferences[PreferencesKeys.TOKEN_KEY] = newToken
        }
    }

    suspend fun clearSession(context: Context) {
        context.sessionDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun getCurrentSession(context: Context, action:(String?) -> Unit){
        context.sessionDataStore.data.collect { preferences ->
            val mToken = preferences[PreferencesKeys.TOKEN_KEY]
            action(mToken)
        }
    }
}
