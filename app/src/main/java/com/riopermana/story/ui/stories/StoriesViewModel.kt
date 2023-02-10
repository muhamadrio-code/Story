package com.riopermana.story.ui.stories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.riopermana.story.R
import com.riopermana.story.data.remote.Retrofit
import com.riopermana.story.model.Story
import com.riopermana.story.ui.BaseViewModel
import com.riopermana.story.ui.utils.DUMMY_TOKEN
import com.riopermana.story.ui.utils.ErrorMessageRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoriesViewModel : BaseViewModel() {
    private val storyApi = Retrofit.storyApi

    private val _stories = MutableLiveData<List<Story>>()
    val stories: LiveData<List<Story>> = _stories

    fun getStories(authorization: String) {
        requestLoadingState()
        viewModelScope.launch {
            val response = runCatching {
                storyApi.getAllStory("Bearer $DUMMY_TOKEN")
            }.getOrNull()

            response ?: run {
                requestErrorAsync(ErrorMessageRes(R.string.connection_failed))
                return@launch
            }

            if (response.isSuccessful) {
                response.body()?.let { storiesResponse ->
                    if (storiesResponse.stories.isEmpty()) {
                        requestErrorAsync(ErrorMessageRes(R.string.no_data))
                    } else {
                        _stories.postValue(storiesResponse.stories)
                    }

                    withContext(Dispatchers.Main) {
                        requestPostLoadingState()
                    }
                } ?: run {
                    requestErrorAsync(ErrorMessageRes(R.string.connection_failed))
                }
            } else {
                requestErrorAsync(ErrorMessageRes(R.string.connection_failed))
            }
        }
    }
}