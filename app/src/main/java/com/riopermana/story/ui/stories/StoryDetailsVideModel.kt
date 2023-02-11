package com.riopermana.story.ui.stories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.riopermana.story.model.Story
import com.riopermana.story.ui.BaseViewModel

class StoryDetailsVideModel : BaseViewModel() {

    private val _observableStory = MutableLiveData<Story>()
    val observableStory: LiveData<Story> = _observableStory

    fun setStory(story: Story){
        _observableStory.value = story
    }
}