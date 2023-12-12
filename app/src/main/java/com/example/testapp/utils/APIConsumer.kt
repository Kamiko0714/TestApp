package com.example.testapp.utils

import com.example.testapp.data.LoginRequestBody
import com.example.testapp.data.LoginResponse
import com.example.testapp.data.RegisterRequestBody
import com.example.testapp.data.RegisterResponse
import com.example.testapp.data.ValidateEmail
import com.example.testapp.data.ValidateEmailResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface APIConsumer {
    @POST("register")
    suspend fun registerUser(@Body body: RegisterRequestBody): Response<RegisterResponse>

    @POST("login")
    suspend fun loginUser(@Body body: LoginRequestBody): Response<LoginResponse>

    @POST("email")
    suspend fun ValidateEmail(@Body body: ValidateEmail): Response<ValidateEmailResponse>
}
