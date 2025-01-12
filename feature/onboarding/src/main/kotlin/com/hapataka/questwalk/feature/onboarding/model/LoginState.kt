package com.hapataka.questwalk.feature.onboarding.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Stable
sealed interface LoginState {
    @Immutable
    data object Idle : LoginState

    @Immutable
    data class Success(val userInfo: UserInfo) : LoginState

    @Immutable
    data object Loading : LoginState

    @Immutable
    data class Failure(val message: String) : LoginState
}

enum class UserInfo {
    EXIST, NONE
}