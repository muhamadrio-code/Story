package com.riopermana.story.model.register

import com.google.gson.annotations.SerializedName

data class SignUpResponse(
    @field:SerializedName("error")
    val isError: Boolean,

    @field:SerializedName("message")
    val message: String
)
