package com.riopermana.story.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val TOKEN_KEY = stringPreferencesKey("com.user.string:token")
    val LOCATION_TOGGLE = booleanPreferencesKey("com.settings.boolean:location-toggle")
    val SHOWCASE_OPTION_KEY = intPreferencesKey("com.settings.int:showcase-option")

    const val SHOWCASE_LIST = 1234
    const val SHOWCASE_MAPS = 5678
}