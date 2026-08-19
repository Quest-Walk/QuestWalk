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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _event = MutableSharedFlow<LoginEvent>()
    val event = _event.asSharedFlow()

    val lastEmail = getLastEmailUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EmailLoginClicked -> loginWithEmail(intent.email, intent.password)
            is LoginIntent.GoogleLoginSucceeded -> loginWithIdToken(intent.idToken)
            LoginIntent.JoinClicked -> emitEvent(LoginEvent.NavigateToJoin)
        }
    }

    private fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _userState.update { UserState.Loading }
            delay(MIN_LOADING_DURATION_MS)

            loginUseCase.withEmail(email, password)
                .onSuccess { checkUserInfo() }
                .onFailure { e ->
                    _userState.update { UserState.LoginFail(e.message.orEmpty()) }
                }
        }
    }

    private fun loginWithIdToken(idToken: String) {
        viewModelScope.launch {
            _userState.update { UserState.Loading }
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
                .onSuccess {
                    _userState.update { UserState.Idle }
                    _event.emit(LoginEvent.NavigateToHome)
                }
                .onFailure {
                    _userState.update { UserState.Idle }
                    _event.emit(LoginEvent.NavigateToSetup)
                }
        }
    }

    private fun emitEvent(event: LoginEvent) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }
}

sealed interface LoginIntent {
    data class EmailLoginClicked(val email: String, val password: String) : LoginIntent
    data class GoogleLoginSucceeded(val idToken: String) : LoginIntent
    data object JoinClicked : LoginIntent
}

sealed interface LoginEvent {
    data object NavigateToHome : LoginEvent
    data object NavigateToSetup : LoginEvent
    data object NavigateToJoin : LoginEvent
}
