package com.riopermana.story.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.PagingData
import com.riopermana.story.model.StoriesResponse
import com.riopermana.story.model.Story
import com.riopermana.story.utils.StoryPagingSource

class FakeStoryRepository : StoryRepository {

    private var list = emptyList<Story>()

    fun setDummyStoriesResponse(data: StoriesResponse) {
        list = data.stories
    }

    override suspend fun getStories(auth: String): LiveData<List<Story>> {
        throw NotImplementedError()
    }

    override fun getStoriesWithPaging(auth: String): LiveData<PagingData<Story>> {
        val data = StoryPagingSource.snapshot(list)
        return liveData { emit(data) }
    }
}