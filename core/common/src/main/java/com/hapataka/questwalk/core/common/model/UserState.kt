package com.hapataka.questwalk.core.common.model

sealed interface UserState {
    data object Idle : UserState

    data class LoggedIn(val userInfo: UserInfo) : UserState

    data object Loading : UserState

    data object LoggedOut : UserState

    data class LoginFail(val message: String) : UserState
}

enum class UserInfo {
    EXIST, NONE
}