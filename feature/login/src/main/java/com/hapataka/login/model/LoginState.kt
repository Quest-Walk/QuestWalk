package com.hapataka.login.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Stable
sealed interface LoginState {
    @Immutable
    data object Idle : LoginState

    @Immutable
    data object Success : LoginState

    @Immutable
    data object Loading : LoginState

    @Immutable
    data class Failure(val message: String) : LoginState
}