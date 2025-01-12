package com.hapataka.questwalk.core.model

sealed interface LoginState {
    data object Idle : LoginState

    data class Success(val userInfo: UserInfo) : LoginState

    data object Loading : LoginState

    data class Failure(val message: String) : LoginState
}

enum class UserInfo {
    EXIST, NONE
}