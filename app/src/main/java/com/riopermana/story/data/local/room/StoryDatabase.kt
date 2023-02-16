package com.riopermana.story.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.riopermana.story.model.RemoteKey
import com.riopermana.story.model.Story

@Database(entities = [Story::class, RemoteKey::class], version = 1, exportSchema = false)
abstract class StoryDatabase : RoomDatabase() {

    abstract fun storyDao() : StoryDao
    abstract fun remoteKeysDao() : RemoteKeysDao
}