package com.hapataka.questwalk.core.ui

sealed class UiState<out T> {
    data object Idle : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Failure(val error: Throwable) : UiState<Nothing>()
}
