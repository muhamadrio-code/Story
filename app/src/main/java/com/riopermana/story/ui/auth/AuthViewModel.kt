package com.riopermana.story.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.riopermana.story.data.remote.Retrofit
import com.riopermana.story.model.User
import com.riopermana.story.ui.BaseViewModel
import com.riopermana.story.ui.auth.login.LoginResponse
import com.riopermana.story.ui.auth.login.LoginResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthViewModel : BaseViewModel() {
    private val storyApi = Retrofit.storyApi

    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse : LiveData<LoginResponse> = _loginResponse

    private val _registerResponse = MutableLiveData<User?>(null)
    val registerResponse : LiveData<User?> = _registerResponse

    fun login(email: String, password: String) {
        requestLoadingState()
        viewModelScope.launch {
            delay(1000L)
            _loginResponse.postValue(LoginResponse(
                error = false, message = "", LoginResult("","","")
            ))
            requestPostLoadingStateAsync()
//            val response = runCatching {
//                storyApi.login(email, password)
//            }.getOrNull()
//
//            if (response == null) {
//                requestErrorAsync(ErrorMessageRes(R.string.sign_in_failed))
//                return@launch
//            }
//
//            if (response.isSuccessful) {
//                _loginResponse.postValue(response.body())
//                requestPostLoadingStateAsync()
//            } else {
//                requestErrorAsync(ErrorMessageRes(R.string.user_not_found))
//            }
        }
    }

    fun createAccount(user: User){
        requestLoadingState()
        viewModelScope.launch {
            delay(1000L)
            _registerResponse.postValue(user)

//            val response = runCatching {
//                storyApi.createUser(
//                    user.name,user.email,user.password
//                )
//            }.getOrNull()
//
//            if (response == null) {
//                requestErrorAsync(ErrorMessageRes(R.string.sign_up_failed))
//                return@launch
//            }
//
//            if (response.isSuccessful) {
//                _registerResponse.postValue(user)
//            } else {
//                requestErrorAsync(ErrorMessageRes(R.string.sign_up_failed))
//            }
        }
    }

    fun requestUpdateUIState() = requestPostLoadingState()
}