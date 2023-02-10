package com.riopermana.story.ui.utils

import androidx.annotation.StringRes

@JvmInline
value class ErrorMessageRes(@StringRes val resId:Int)

sealed interface UiState {
    object OnLoading : UiState
    data class OnError(val message: ErrorMessageRes) : UiState
    object OnPostLoading : UiState
}