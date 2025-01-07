package com.hapataka.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bongpal.domain.LoginUseCase
import com.hapataka.login.model.LoginState
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
) : ViewModel() {
    private val _loginState: MutableStateFlow<LoginState> = MutableStateFlow(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _loginState.update { LoginState.Loading }

            delay(1000)

            loginUseCase(email, password)
                .onSuccess { _loginState.update { LoginState.Success } }
                .onFailure { e -> _loginState.update { LoginState.Failure(e.message.orEmpty()) } }
        }
    }
}