package com.example.testapp.view_model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.data.RegisterRequestBody
import com.example.testapp.data.User
import com.example.testapp.data.ValidateEmail
import com.example.testapp.repository.AuthRepository
import com.example.testapp.utils.AuthToken
import com.example.testapp.utils.RequestStatus
import kotlinx.coroutines.launch

class RegisterActivityViewModel(
    private val authRepository: AuthRepository,
    private val application: Application
) : ViewModel() {
    private val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    private val errorMessage: MutableLiveData<HashMap<String, String>> = MutableLiveData()
    private val isUnique: MutableLiveData<Boolean> = MutableLiveData(false)
    private val user: MutableLiveData<User> = MutableLiveData()

    fun getIsLoading(): LiveData<Boolean> = isLoading
    fun getErrorMessage(): LiveData<HashMap<String, String>> = errorMessage
    fun getIsUnique(): LiveData<Boolean> = isUnique
    fun getUser(): LiveData<User> = user

    fun validateEmailAddress(body: ValidateEmail) {
        viewModelScope.launch {
            authRepository.ValidateEmailAddress(body).collect {
                when (it) {
                    is RequestStatus.Waiting -> {
                        isLoading.value = true
                    }

                    is RequestStatus.Success -> {
                        isLoading.value = false
                        isUnique.value = it.data.isUnique
                    }

                    is RequestStatus.Error -> {
                        isLoading.value = false
                        errorMessage.value = it.message
                    }
                }
            }
        }
    }

    fun registerUser(body: RegisterRequestBody) {
        viewModelScope.launch {
            authRepository.registerUser(body).collect {
                when (it) {
                    is RequestStatus.Waiting -> {
                        isLoading.value = true
                    }

                    is RequestStatus.Success -> {
                        isLoading.value = false
                        user.value = it.data.user
                        AuthToken.getInstance(application.baseContext).token = it.data.token
                    }

                    is RequestStatus.Error -> {
                        isLoading.value = false
                        errorMessage.value = it.message
                    }
                }
            }
        }
    }
}
