package com.riopermana.story.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.riopermana.story.repositories.StoryRepository
import com.riopermana.story.ui.stories.StoriesViewModel
import com.riopermana.story.ui.utils.ErrorMessageRes
import com.riopermana.story.ui.utils.UiState

open class BaseViewModel: ViewModel() {

    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState

    protected fun requestLoadingState() {
        _uiState.value = UiState.OnLoading
    }

    protected fun requestErrorAsync(errorMessageRes: ErrorMessageRes) {
        _uiState.postValue(UiState.OnError(errorMessageRes))
    }

    protected fun requestPostLoadingState() {
        _uiState.value = UiState.OnPostLoading
    }

    protected fun requestPostLoadingStateAsync() {
        _uiState.postValue(UiState.OnPostLoading)
    }
}

class ViewModelFactory(private val storyRepository: StoryRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoriesViewModel::class.java)) {
            return StoriesViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}