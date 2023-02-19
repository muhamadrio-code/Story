package com.riopermana.story.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.riopermana.story.data.StoryRemoteMediator
import com.riopermana.story.data.local.room.StoryDatabase
import com.riopermana.story.data.remote.StoryApiService
import com.riopermana.story.model.Story

class StoriesRepositoryImpl(
    private val storyDatabase: StoryDatabase,
    private val storyApiService: StoryApiService
) : StoryRepository {

    override suspend fun getStories(auth: String): LiveData<List<Story>> {
        val response = runCatching {
            storyApiService.getStoriesWithLocation("Bearer $auth")
        }.getOrNull()
        val stories = response?.body()?.stories ?: emptyList()
        return liveData {
            emit(stories)
        }
    }

    @ExperimentalPagingApi
    override fun getStoriesWithPaging(auth: String): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                enablePlaceholders = true,
                initialLoadSize = DEFAULT_PAGE_SIZE,
                prefetchDistance = 0
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