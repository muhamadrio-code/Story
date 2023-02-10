package com.riopermana.story.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.riopermana.story.data.local.PreferencesKeys
import com.riopermana.story.data.local.sessionDataStore
import com.riopermana.story.databinding.ActivityAuthBinding
import com.riopermana.story.ui.stories.StoryActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        lifecycleScope.launch {
            sessionDataStore.data.collect { preferences ->
                val token = preferences[PreferencesKeys.TOKEN_STRING]
                withContext(Dispatchers.Main.immediate) {
                    if (token == null) {
                        setContentView(binding.root)
                    } else {
                        val intent = Intent(this@AuthActivity, StoryActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }
}