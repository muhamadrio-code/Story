package com.riopermana.story.data.remote

import com.riopermana.story.model.ApiResponse
import com.riopermana.story.model.StoriesResponse
import com.riopermana.story.ui.auth.login.LoginResponse
import retrofit2.Response
import retrofit2.http.*

interface StoryApi {

    @POST("v1/register")
    @FormUrlEncoded
    suspend fun createUser(
        @Field("name") name:String,
        @Field("email") email:String,
        @Field("password") password: String
    ) : Response<ApiResponse>

    @POST("v1/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("email") email:String,
        @Field("password") password: String
    ) : Response<LoginResponse>

    @GET("v1/stories")
    suspend fun getAllStory(@Header("Authorization") authorization:String) : Response<StoriesResponse>
}