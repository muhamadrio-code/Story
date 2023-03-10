package com.riopermana.story.ui.stories

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.riopermana.story.R
import com.riopermana.story.data.local.PreferencesKeys
import com.riopermana.story.data.local.sessionDataStore
import com.riopermana.story.ui.auth.AuthActivity
import kotlinx.coroutines.Dispatchers

class StoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        sessionDataStore.data
            .asLiveData(lifecycleScope.coroutineContext + Dispatchers.Main.immediate)
            .observe(this@StoryActivity) {preferences ->
                val token = preferences[PreferencesKeys.TOKEN_KEY]
                if(token == null){
                    startActivity(Intent(this@StoryActivity, AuthActivity::class.java))
                    finish()
                }
            }

        setContentView(R.layout.activity_story)

    }
}