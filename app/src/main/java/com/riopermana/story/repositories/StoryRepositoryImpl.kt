package com.riopermana.story.repositories

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.riopermana.story.data.StoryRemoteMediator
import com.riopermana.story.data.local.room.StoryDatabase
import com.riopermana.story.data.remote.StoryApiService
import com.riopermana.story.model.Story

class StoryRepositoryImpl(
    private val storyDatabase: StoryDatabase,
    private val storyApiService: StoryApiService
) : StoryRepository {

    @ExperimentalPagingApi
    override fun getStories(auth: String): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE
            ),
            remoteMediator = StoryRemoteMediator(
                authKey = auth,
                database = storyDatabase,
                storyApiService = storyApiService
            ),
            pagingSourceFactory = {
                storyDatabase.storyDao().getStories()
            }
        ).liveData
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 5
    }
}