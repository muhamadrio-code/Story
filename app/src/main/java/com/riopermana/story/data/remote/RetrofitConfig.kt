package com.riopermana.story.data.remote

import com.google.gson.GsonBuilder
import com.riopermana.story.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitConfig {

    private val client = OkHttpClient.Builder().apply {
        val loggingInterceptor = if(BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }
        addInterceptor(loggingInterceptor)
    }.build()

    private val instance: retrofit2.Retrofit by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                GsonBuilder().serializeNulls().create()
            ))
            .client(client)
            .build()
    }


    val storyApi: StoryApi by lazy {
        instance.create(StoryApi::class.java)
    }
}