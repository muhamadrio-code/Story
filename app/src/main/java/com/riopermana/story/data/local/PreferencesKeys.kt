package com.riopermana.story.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val TOKEN_KEY = stringPreferencesKey("com.user.string:token")
    val LOCATION_TOGGLE = booleanPreferencesKey("com.settings.boolean:location-toggle")
}