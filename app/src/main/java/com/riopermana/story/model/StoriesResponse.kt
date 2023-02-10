package com.riopermana.story.model

import com.google.gson.annotations.SerializedName

data class StoriesResponse(
    @field:SerializedName("error")
    val isError: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("listStory")
    val stories: List<Story> = emptyList(),
)