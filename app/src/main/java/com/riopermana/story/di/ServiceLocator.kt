package com.riopermana.story.di

import android.app.Application
import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.riopermana.story.data.local.room.StoryDatabase
import com.riopermana.story.data.remote.RetrofitConfig
import com.riopermana.story.repositories.StoryRepositoryImpl

val Application.storyRepository get() = ServiceLocator.provideTasksRepository(this)


object ServiceLocator {

    private var database: StoryDatabase? = null

    @Volatile
    var tasksRepository: StoryRepositoryImpl? = null
        @VisibleForTesting set

    fun provideTasksRepository(context: Context): StoryRepositoryImpl {
        synchronized(this) {
            return tasksRepository ?: run {
                val repo = createStoryRepository(context)
                tasksRepository = repo
                repo
            }
        }
    }

    private fun createStoryRepository(context: Context) : StoryRepositoryImpl {
        return StoryRepositoryImpl(
            createDataBase(context.applicationContext),
            RetrofitConfig.storyApiService
        )
    }

    private fun createDataBase(context: Context): StoryDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            StoryDatabase::class.java, "Story.db"
        ).build()
        database = result
        return result
    }

}