package com.example.testapp.storydata
import com.google.gson.annotations.SerializedName

data class Story(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("photoUrl") val photoUrl: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double
)
