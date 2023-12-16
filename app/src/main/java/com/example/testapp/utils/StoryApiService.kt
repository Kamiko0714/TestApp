package com.example.testapp.utils
import com.example.testapp.storydata.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface StoryApiService {

    @Headers("Content-Type: multipart/form-data")
    @Multipart
    @POST("/stories")
    fun addNewStory(
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part?,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?,
        @Header("Authorization") authorization: String
    ): Call<StoryResponse>

    @Multipart
    @POST("/stories/guest")
    fun addNewStoryGuest(
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part?,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): Call<StoryResponse>

    @GET("/stories")
    fun getAllStories(
        @Query("page") page: Int?,
        @Query("size") size: Int?,
        @Query("location") location: Int?,
        @Header("Authorization") authorization: String
    ): Call<StoryResponse>

    @GET("/stories/{id}")
    fun getStoryDetail(
        @Path("id") storyId: String,
        @Header("Authorization") authorization: String
    ): Call<StoryResponse>
}
