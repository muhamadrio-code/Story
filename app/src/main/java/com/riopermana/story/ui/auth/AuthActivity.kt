package com.riopermana.story.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.riopermana.story.R
import com.riopermana.story.data.local.PreferencesKeys
import com.riopermana.story.data.local.sessionDataStore
import com.riopermana.story.ui.stories.StoryActivity
import kotlinx.coroutines.Dispatchers

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionDataStore.data
            .asLiveData(lifecycleScope.coroutineContext + Dispatchers.Main.immediate)
            .observe(this@AuthActivity) {preferences ->
                val token = preferences[PreferencesKeys.TOKEN_KEY]
                if (token == null) {
                    setContentView(R.layout.activity_auth)
                } else {
                    val intent = Intent(this@AuthActivity, StoryActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
    }
}