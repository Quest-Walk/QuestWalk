package com.hapataka.questwalk.feature.onboarding.screen.setup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.domain.usecase.GetUserInfoUseCase
import com.hapataka.questwalk.core.domain.usecase.LogoutUseCase
import com.hapataka.questwalk.core.domain.usecase.PostUserInfoUserCase
import com.hapataka.questwalk.core.model.CharacterType
import com.hapataka.questwalk.feature.onboarding.model.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val postUserInfoUserCase: PostUserInfoUserCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {
    private val _userState = MutableStateFlow<UserState>(UserState.Idle)
    val loginState = _userState.asStateFlow()

    private val _event = MutableSharedFlow<SetupEvent>()
    val event = _event.asSharedFlow()

    init {
        checkExistingUser()
    }

    fun onIntent(intent: SetupIntent) {
        when (intent) {
            is SetupIntent.DoneClicked -> postUserInfo(intent.userName, intent.characterType)
            SetupIntent.LogoutClicked -> logout(navigateToLogin = true)
        }
    }

    private fun checkExistingUser() {
        viewModelScope.launch {
            try {
                val user = getUserInfoUseCase().first()
                Log.d("SetupViewModel", "checkExistingUser: user=$user")
                if (user != null && user.userName.isNotBlank()) {
                    Log.d("SetupViewModel", "User exists, navigating to home")
                    _event.emit(SetupEvent.NavigateToHome)
                } else {
                    Log.d("SetupViewModel", "No existing user, show setup screen")
                }
            } catch (e: Exception) {
                Log.e("SetupViewModel", "getUserInfo failed: ${e.message}")
            }
        }
    }

    private fun postUserInfo(userName: String, characterType: CharacterType) {
        viewModelScope.launch {
            Log.d("SetupViewModel", "postUserInfo called: userName='$userName'")
            _userState.update { UserState.Loading }
            delay(500)
            postUserInfoUserCase(userName, characterType)
                .onSuccess {
                    Log.d("SetupViewModel", "postUserInfo success, navigating to home")
                    _userState.update { UserState.Idle }
                    _event.emit(SetupEvent.NavigateToHome)
                }
                .onFailure { e ->
                    Log.e("SetupViewModel", "postUserInfo failed: ${e.message}")
                    _userState.update { UserState.LoginFail(e.message.orEmpty()) }
                }
        }
    }

    private fun logout(navigateToLogin: Boolean) {
        viewModelScope.launch {
            logoutUseCase()
            if (navigateToLogin) {
                _event.emit(SetupEvent.NavigateToLogin)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("logoutTest", "SetupViewModel Cleared")
    }
}

sealed interface SetupIntent {
    data class DoneClicked(
        val userName: String,
        val characterType: CharacterType,
    ) : SetupIntent

    data object LogoutClicked : SetupIntent
}

sealed interface SetupEvent {
    data object NavigateToHome : SetupEvent
    data object NavigateToLogin : SetupEvent
}
