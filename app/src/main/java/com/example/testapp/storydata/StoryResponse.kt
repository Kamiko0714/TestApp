package com.example.testapp.storydata
import com.google.gson.annotations.SerializedName

data class StoryResponse(
    @SerializedName("error") val error: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("listStory") val listStory: List<Story>
)
