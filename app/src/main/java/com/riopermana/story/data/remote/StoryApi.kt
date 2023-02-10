package com.riopermana.story.data.remote

import com.riopermana.story.model.register.SignUpResponse
import com.riopermana.story.ui.auth.login.LoginResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface StoryApi {

    @POST("v1/register")
    @FormUrlEncoded
    suspend fun createUser(
        @Field("name") name:String,
        @Field("email") email:String,
        @Field("password") password: String
    ) : Response<SignUpResponse>

    @POST("v1/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("email") email:String,
        @Field("password") password: String
    ) : Response<LoginResponse>

}