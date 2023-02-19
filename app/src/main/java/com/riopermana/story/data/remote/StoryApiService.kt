package com.riopermana.story.data.remote

import com.riopermana.story.model.ApiResponse
import com.riopermana.story.model.StoriesResponse
import com.riopermana.story.ui.auth.login.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface StoryApiService {

    @POST("v1/register")
    @FormUrlEncoded
    suspend fun createUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<ApiResponse>

    @POST("v1/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @GET("v1/stories")
    suspend fun getStories(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<StoriesResponse>

    @GET("v1/stories?location=1")
    suspend fun getStoriesWithLocation(
        @Header("Authorization") authorization: String
    ): Response<StoriesResponse>

    @Multipart
    @POST("v1/stories")
    suspend fun uploadFile(
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null,
        @Part file: MultipartBody.Part,
        @Header("Authorization") authorization: String
    ): Response<ApiResponse>

}