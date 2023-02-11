package com.riopermana.story.ui.stories

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.riopermana.story.R
import com.riopermana.story.data.remote.Retrofit
import com.riopermana.story.ui.BaseViewModel
import com.riopermana.story.ui.utils.ErrorMessageRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class NewStoryViewModel : BaseViewModel() {
    private val storyApi = Retrofit.storyApi

    private val _observableUploadResponse = MutableLiveData<Boolean>()
    val observableUploadResponse: LiveData<Boolean> = _observableUploadResponse

    var currentUri: Uri? = null
        private set

    private var tempUri: Uri? = null

    private val _observableUri = MutableLiveData<Uri?>(null)
    val observableUri : LiveData<Uri?> =_observableUri

    fun uploadFile(file: MultipartBody.Part, description: RequestBody, auth:String) {
        requestLoadingState()
        viewModelScope.launch {
            val response = runCatching {
                storyApi.uploadFile(
                    description = description,
                    file = file,
                    authorization = "Bearer $auth"
                )
            }.getOrNull()

            if (response == null) {
                requestErrorAsync(ErrorMessageRes(R.string.upload_failed))
                return@launch
            }

            if (response.isSuccessful) {
                _observableUploadResponse.postValue(response.isSuccessful)
                withContext(Dispatchers.Main) {
                    requestPostLoadingState()
                }
            } else {
                requestErrorAsync(ErrorMessageRes(R.string.upload_failed))
            }
        }
    }

    fun onPostTakePicture(isSuccess:Boolean) {
        if (isSuccess) {
            _observableUri.value = tempUri
            currentUri = tempUri
        }
        tempUri = null
    }

    fun setTempUri(uri: Uri) {
        tempUri = uri
    }

    fun setCurrentUri(uri: Uri) {
        currentUri = uri
        _observableUri.value = uri
    }
}