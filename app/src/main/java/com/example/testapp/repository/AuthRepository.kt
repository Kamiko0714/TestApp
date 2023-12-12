package com.example.testapp.repository

import com.example.testapp.data.LoginRequestBody
import com.example.testapp.data.RegisterRequestBody
import com.example.testapp.data.ValidateEmail
import com.example.testapp.data.ValidateEmailResponse
import com.example.testapp.utils.APIConsumer
import com.example.testapp.utils.RequestStatus
import com.example.testapp.utils.SimplifiedMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepository(val consumer: APIConsumer) {
    fun ValidateEmailAddress(body: ValidateEmail): Flow<RequestStatus<ValidateEmailResponse>> = flow {
        emit(RequestStatus.Waiting)
        val response = consumer.ValidateEmail(body)
        if (response.isSuccessful) {
            emit((RequestStatus.Success(response.body()!!)))
        } else {
            emit(
                RequestStatus.Error(
                    SimplifiedMessage.get(
                        response.errorBody()!!.byteStream().reader().readText()
                    )
                )
            )
        }
    }
    fun registerUser(body: RegisterRequestBody) = flow {
        emit(RequestStatus.Waiting)
        val response = consumer.registerUser(body)
        if (response.isSuccessful) {
            emit((RequestStatus.Success(response.body()!!)))
        } else {
            emit(
                RequestStatus.Error(
                    SimplifiedMessage.get(
                        response.errorBody()!!.byteStream().reader().readText()
                    )
                )
            )
        }
    }
    fun loginUser(body: LoginRequestBody) = flow {
        emit(RequestStatus.Waiting)
        val response = consumer.loginUser(body)
        if (response.isSuccessful) {
            emit((RequestStatus.Success(response.body()!!)))
        } else {
            emit(
                RequestStatus.Error(
                    SimplifiedMessage.get(
                        response.errorBody()!!.byteStream().reader().readText()
                    )
                )
            )
        }
    }
}