package com.hapataka.questwalk.feature.onboarding.screen.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.domain.usecase.GetLoginUserIdUseCase
import com.hapataka.questwalk.core.domain.usecase.GetUserInfoUseCase
import com.hapataka.questwalk.core.domain.usecase.LoginUseCase
import com.hapataka.questwalk.feature.onboarding.model.UserInfo
import com.hapataka.questwalk.feature.onboarding.model.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val getLoginUserIdUseCase: GetLoginUserIdUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
) : ViewModel() {
    private val _userState: MutableStateFlow<UserState> = MutableStateFlow(UserState.Idle)
    val userState = _userState.asStateFlow()

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _userState.update { UserState.Loading }

            delay(500)

            loginUseCase(email, password)
                .onSuccess { uid ->
                    checkUserInfo(uid)
                }
                .onFailure { e ->
                    _userState.update { UserState.LoginFail(e.message.orEmpty()) }
                    Log.e(javaClass.name, "Fatal: " + e.message.orEmpty())
                }
        }
    }

    fun loginWithIdToken(idToken: String) {
        viewModelScope.launch {
            _userState.update { UserState.Loading }

            delay(500)

            loginUseCase(idToken)
                .onSuccess { uid ->
                    checkUserInfo(uid)
                }
                .onFailure { e ->
                    _userState.update { UserState.LoginFail(e.message.orEmpty()) }
                    Log.e(javaClass.name, "Fatal: " + e.message.orEmpty())
                }
        }
    }

    private fun checkUserInfo(uid: String) {
        viewModelScope.launch {
            getUserInfoUseCase(uid)
                .onSuccess {
                    _userState.update { UserState.LoggedIn(UserInfo.EXIST) }
                }
                .onFailure {
                    _userState.update { UserState.LoggedIn(UserInfo.NONE) }
                }
            this@LoginViewModel.onCleared()
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("navigationTest", "onCleared")
    }
}