package com.riopermana.story.data.remote

import com.google.gson.GsonBuilder
import com.riopermana.story.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {

    var BASE_URL = BuildConfig.BASE_URL

    private val client = OkHttpClient.Builder().apply {
        val loggingInterceptor = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }
        addInterceptor(loggingInterceptor)
    }.build()

    private val instance: retrofit2.Retrofit by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                GsonBuilder().serializeNulls().create()
            ))
            .client(client)
            .build()
    }


    val storyApiService: StoryApiService by lazy {
        instance.create(StoryApiService::class.java)
    }
}