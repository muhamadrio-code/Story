package com.riopermana.story.model

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @field:SerializedName("error")
    val isError: Boolean,

    @field:SerializedName("message")
    val message: String
)
