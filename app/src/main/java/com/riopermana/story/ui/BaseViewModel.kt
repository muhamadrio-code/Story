package com.riopermana.story.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.riopermana.story.ui.utils.ErrorMessageRes
import com.riopermana.story.ui.utils.UiState

open class BaseViewModel: ViewModel() {

    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState

    protected fun requestLoadingState() {
        _uiState.value = UiState.OnLoading
    }

    protected fun requestErrorAsync(errorMessageRes: ErrorMessageRes) {
        _uiState.postValue(UiState.OnError(errorMessageRes))
    }

    protected fun requestPostLoadingState() {
        _uiState.value = UiState.OnPostLoading
    }

    protected fun requestPostLoadingStateAsync() {
        _uiState.postValue(UiState.OnPostLoading)
    }

}