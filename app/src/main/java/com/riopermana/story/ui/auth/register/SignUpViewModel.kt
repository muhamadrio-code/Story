package com.riopermana.story.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.riopermana.story.R
import com.riopermana.story.data.remote.Retrofit
import com.riopermana.story.model.User
import com.riopermana.story.ui.BaseViewModel
import com.riopermana.story.ui.utils.ErrorMessageRes
import com.riopermana.story.ui.auth.login.LoginResponse
import kotlinx.coroutines.launch

class SignUpViewModel : BaseViewModel() {
    private val storyApi = Retrofit.storyApi

    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse : LiveData<LoginResponse> = _loginResponse

    private fun login(user: User) {
        viewModelScope.launch {
            val response = runCatching {
                storyApi.login(user.email, user.password)
            }.getOrNull()

            if (response == null) {
                requestErrorAsync(ErrorMessageRes(R.string.sign_in_failed))
                return@launch
            }

            if (response.isSuccessful) {
                requestPostLoadingStateAsync()
            } else {
                requestErrorAsync(ErrorMessageRes(R.string.user_not_found))
            }
        }
    }

    fun signUp(user: User){
        requestLoadingState()
        viewModelScope.launch {
            val response = runCatching {
                storyApi.createUser(
                    user.name,user.email,user.password
                )
            }.getOrNull()

            if (response == null) {
                requestErrorAsync(ErrorMessageRes(R.string.sign_up_failed))
                return@launch
            }

            if (response.isSuccessful) {
                login(user)
            } else {
                requestErrorAsync(ErrorMessageRes(R.string.sign_up_failed))
            }
        }
    }

    fun requestUpdateUIState() = requestPostLoadingState()
}