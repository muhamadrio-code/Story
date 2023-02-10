package com.riopermana.story.ui.auth.login

import com.google.gson.annotations.SerializedName


data class LoginResponse(
    @field:SerializedName("error")
    val error: Boolean,
    @field:SerializedName("message")
    val message:String,
    @field:SerializedName("loginResult")
    val loginResult: LoginResult? = null,
)