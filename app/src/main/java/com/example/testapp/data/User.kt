package com.example.testapp.data

import com.google.gson.annotations.SerializedName

data class User(@SerializedName("userId") val userId: String, val name: String, val email: String)