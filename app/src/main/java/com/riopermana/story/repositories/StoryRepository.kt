package com.riopermana.story.repositories

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.riopermana.story.model.Story

interface StoryRepository {

    suspend fun getStories(auth: String): LiveData<List<Story>>
    fun getStoriesWithPaging(auth: String): LiveData<PagingData<Story>>

}