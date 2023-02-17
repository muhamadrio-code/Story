package com.riopermana.story.ui.stories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.riopermana.story.model.Story
import com.riopermana.story.repositories.StoryRepository
import com.riopermana.story.ui.BaseViewModel
import com.riopermana.story.ui.utils.ErrorMessageRes

class StoriesViewModel(private val storyRepository: StoryRepository) : BaseViewModel() {

    private val _stories = MutableLiveData<String>()
    val stories: LiveData<PagingData<Story>> = _stories.switchMap { auth ->
        storyRepository.getStories("Bearer $auth").cachedIn(viewModelScope)
    }

    private val _fabsInvisibility = MutableLiveData<Boolean>()
    val fabsInvisibility: LiveData<Boolean> = _fabsInvisibility

    private var isInvisible = true
    fun toggleFabsInvisibility() {
        _fabsInvisibility.value = isInvisible
        isInvisible = !isInvisible
    }

    fun getStories(auth: String) {
        _stories.value = auth
    }

    fun requestLoading() = requestLoadingState()
    fun requestNotLoading() = requestPostLoadingState()
    fun requestErrorState(errorMessageRes: ErrorMessageRes) = requestErrorAsync(errorMessageRes)
}
