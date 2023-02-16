package com.riopermana.story.data.local.room

import androidx.paging.PagingSource
import androidx.room.*
import com.riopermana.story.model.Story

@Dao
interface StoryDao {

    @Query("SELECT * FROM story_table")
    fun getStories() : PagingSource<Int, Story>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<Story>)

    @Query("DELETE FROM story_table")
    suspend fun clearStories()
}