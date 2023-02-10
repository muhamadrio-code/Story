package com.riopermana.story.ui.stories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.riopermana.story.data.remote.Retrofit
import com.riopermana.story.model.Story
import com.riopermana.story.ui.BaseViewModel

class StoryDetailsVideModel : BaseViewModel() {
    private val storyApi = Retrofit.storyApi

    private val _observableStory = MutableLiveData<Story>()
    val observableStory: LiveData<Story> = _observableStory

    fun setStory(story: Story){
        _observableStory.value = story
    }

//    fun getStory(authorization: String) {
//        requestLoadingState()
//        viewModelScope.launch {
//            val response = runCatching {
//                storyApi.getAllStory("Bearer $DUMMY_TOKEN")
//            }.getOrNull()
//
//            response ?: run {
//                requestErrorAsync(ErrorMessageRes(R.string.connection_failed))
//                return@launch
//            }
//
//            if (response.isSuccessful) {
//                response.body()?.let { storiesResponse ->
//                    if (storiesResponse.stories.isEmpty()) {
//                        requestErrorAsync(ErrorMessageRes(R.string.no_data))
//                    } else {
//                        _stories.postValue(storiesResponse.stories)
//                    }
//
//                    withContext(Dispatchers.Main) {
//                        requestPostLoadingState()
//                    }
//                } ?: run {
//                    requestErrorAsync(ErrorMessageRes(R.string.connection_failed))
//                }
//            } else {
//                requestErrorAsync(ErrorMessageRes(R.string.connection_failed))
//            }
//        }
//    }
}