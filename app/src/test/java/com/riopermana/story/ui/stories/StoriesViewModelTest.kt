package com.riopermana.story.ui.stories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import com.riopermana.story.model.Story
import com.riopermana.story.repositories.FakeStoryRepository
import com.riopermana.story.ui.adapters.StoryPagingAdapter
import com.riopermana.story.utils.MainDispatcherRule
import com.riopermana.story.utils.getOrAwaitValue
import com.riopermana.story.utils.noopListUpdateCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class StoriesViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    private lateinit var fakeStoryRepository: FakeStoryRepository
    private lateinit var storiesViewModel: StoriesViewModel

    @Before
    fun setup() {
        fakeStoryRepository = FakeStoryRepository()
        storiesViewModel = StoriesViewModel(fakeStoryRepository)
    }

    @Test
    fun `when get stories should not null and return data`() = runTest {
        val dummyData = StoryDataDummy.generateDummyStoryResponse(false)
        fakeStoryRepository.setDummyStoriesResponse(dummyData)

        storiesViewModel.getStories("auth-key")
        val actualStory: PagingData<Story> = storiesViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryPagingAdapter.StoryDiffUtil(),
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStory)
        assertNotNull(differ.snapshot())
    }

    @Test
    fun `when get stories validate data size and return data`() = runTest {
        val dummyData = StoryDataDummy.generateDummyStoryResponse(false)
        fakeStoryRepository.setDummyStoriesResponse(dummyData)

        storiesViewModel.getStories("auth-key")
        val actualStory: PagingData<Story> = storiesViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryPagingAdapter.StoryDiffUtil(),
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStory)
        assertEquals(differ.snapshot().size, dummyData.stories.size)
    }

    @Test
    fun `when get stories validate first data and return data`() = runTest {
        val dummyData = StoryDataDummy.generateDummyStoryResponse(false)
        fakeStoryRepository.setDummyStoriesResponse(dummyData)

        storiesViewModel.getStories("auth-key")
        val actualStory: PagingData<Story> = storiesViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryPagingAdapter.StoryDiffUtil(),
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStory)
        assertEquals(differ.snapshot().items[0], dummyData.stories[0])
    }

    @Test
    fun `when get stories validate data size is zero and return no data`() = runTest {
        val dummyData = StoryDataDummy.generateDummyStoryResponse(true)
        fakeStoryRepository.setDummyStoriesResponse(dummyData)

        storiesViewModel.getStories("auth-key")
        val actualStory: PagingData<Story> = storiesViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryPagingAdapter.StoryDiffUtil(),
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStory)
        assertEquals(differ.snapshot().size, dummyData.stories.size)
    }

}
