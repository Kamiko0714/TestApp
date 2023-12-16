package com.example.testapp.utils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object StoryApiClient {

    private const val BASE_URL = "https://story-api.dicoding.dev/v1/"

    val storyApiService: StoryApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StoryApiService::class.java)
    }
}
