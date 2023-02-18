package com.riopermana.story.ui.maps

import androidx.lifecycle.*
import com.riopermana.story.model.Story
import com.riopermana.story.repositories.StoryRepository

class MapsViewModel(private val repository: StoryRepository) : ViewModel() {
    private val _stories = MutableLiveData<String>()
    val stories: LiveData<List<Story>> = _stories.switchMap { auth ->
        liveData {
            emitSource(repository.getStories(auth))
        }
    }

    fun getStories(auth: String) {
        _stories.value = auth
    }
}