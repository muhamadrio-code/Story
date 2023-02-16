package com.riopermana.story.ui.stories

import com.riopermana.story.model.StoriesResponse
import com.riopermana.story.model.Story

object StoryDataDummy {
    fun generateDummyStoryResponse(isError: Boolean): StoriesResponse {
        val stories = buildList {
            for (i in 0..99) {
                val story = Story(
                    createdAt = "",
                    description = "data ke $i",
                    id = i.toString(),
                    lat = i.toDouble(),
                    lon = i.toDouble() + 2,
                    name = "name $i",
                    photoUrl = "https://story.com/image-$i"
                )
                add(story)
            }
        }

        return if (isError) {
            StoriesResponse(true, "Error")
        } else {
            StoriesResponse(false, "Success", stories)
        }
    }
}