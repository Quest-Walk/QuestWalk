package com.hapataka.questwalk.feature.onboarding.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.domain.usecase.FetchUserInfoUseCase
import com.hapataka.questwalk.core.domain.usecase.GetLastEmailUseCase
import com.hapataka.questwalk.core.domain.usecase.LoginUseCase
import com.hapataka.questwalk.feature.onboarding.model.UserInfo
import com.hapataka.questwalk.feature.onboarding.model.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MIN_LOADING_DURATION_MS = 500L

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val fetchUserInfoUseCase: FetchUserInfoUseCase,
    getLastEmailUseCase: GetLastEmailUseCase,
) : ViewModel() {
    private val _userState: MutableStateFlow<UserState> = MutableStateFlow(UserState.Idle)
    val userState = _userState.asStateFlow()

    val lastEmail = getLastEmailUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _userState.update { UserState.Loading }
            // 로딩 UI가 너무 빠르게 사라지지 않도록 최소 로딩 시간 보장
            delay(MIN_LOADING_DURATION_MS)

            loginUseCase.withEmail(email, password)
                .onSuccess { checkUserInfo() }
                .onFailure { e ->
                    _userState.update { UserState.LoginFail(e.message.orEmpty()) }
                }
        }
    }

    fun loginWithIdToken(idToken: String) {
        viewModelScope.launch {
            _userState.update { UserState.Loading }
            // 로딩 UI가 너무 빠르게 사라지지 않도록 최소 로딩 시간 보장
            delay(MIN_LOADING_DURATION_MS)

            loginUseCase.withGoogle(idToken)
                .onSuccess { checkUserInfo() }
                .onFailure { e ->
                    _userState.update { UserState.LoginFail(e.message.orEmpty()) }
                }
        }
    }

    private fun checkUserInfo() {
        viewModelScope.launch {
            fetchUserInfoUseCase()
                .onSuccess { _userState.update { UserState.LoggedIn(UserInfo.EXIST) } }
                .onFailure { _userState.update { UserState.LoggedIn(UserInfo.NONE) } }
        }
    }
}