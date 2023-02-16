package com.riopermana.story.repositories

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.riopermana.story.model.Story

interface StoryRepository {

    fun getStories(auth:String) : LiveData<PagingData<Story>>

}