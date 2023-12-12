package com.example.testapp.data

data class RegisterResponse(
    val error: Boolean,
    val message: String,
    val user: User,
    val token: String?
)


