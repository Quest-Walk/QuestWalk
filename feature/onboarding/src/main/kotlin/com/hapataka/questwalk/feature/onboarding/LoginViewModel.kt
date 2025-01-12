package com.hapataka.questwalk.feature.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.domain.usecase.GetLoginUserIdUseCase
import com.hapataka.questwalk.core.domain.usecase.GetUserInfoUseCase
import com.hapataka.questwalk.core.domain.usecase.LoginUseCase
import com.hapataka.questwalk.core.model.LoginState
import com.hapataka.questwalk.core.model.UserInfo
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
    private val _loginState: MutableStateFlow<LoginState> = MutableStateFlow(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _loginState.update { LoginState.Loading }

            delay(1000)

            loginUseCase(email, password)
                .onSuccess { uid ->
                    checkUserInfo(uid)
                }
                .onFailure { e ->
                    _loginState.update { LoginState.Failure(e.message.orEmpty()) }
                    Log.e(javaClass.name, "Fatal: " + e.message.orEmpty())
                }
        }
    }

    private fun checkUserInfo(uid: String) {
        viewModelScope.launch {
            getUserInfoUseCase(uid)
                .onSuccess {
                    _loginState.update { LoginState.Success(UserInfo.EXIST) }
                }
                .onFailure {
                    _loginState.update { LoginState.Success(UserInfo.NONE) }
                }
        }
    }
}