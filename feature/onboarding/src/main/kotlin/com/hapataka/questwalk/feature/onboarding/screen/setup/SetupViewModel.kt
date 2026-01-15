package com.hapataka.questwalk.feature.onboarding.screen.setup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.domain.usecase.GetUserInfoUseCase
import com.hapataka.questwalk.core.domain.usecase.LogoutUseCase
import com.hapataka.questwalk.core.domain.usecase.PostUserInfoUserCase
import com.hapataka.questwalk.core.model.CharacterType
import com.hapataka.questwalk.feature.onboarding.model.UserInfo
import com.hapataka.questwalk.feature.onboarding.model.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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

    init {
        checkExistingUser()
    }

    private fun checkExistingUser() {
        viewModelScope.launch {
            try {
                val user = getUserInfoUseCase().first()
                Log.d("SetupViewModel", "checkExistingUser: user=$user")
                if (user != null && user.userName.isNotBlank()) {
                    Log.d("SetupViewModel", "User exists, navigating to home")
                    _userState.update { UserState.LoggedIn(UserInfo.EXIST) }
                } else {
                    Log.d("SetupViewModel", "No existing user, show setup screen")
                }
            } catch (e: Exception) {
                Log.e("SetupViewModel", "getUserInfo failed: ${e.message}")
            }
        }
    }

    fun postUserInfo(userName: String, characterType: CharacterType) {
        viewModelScope.launch {
            Log.d("SetupViewModel", "postUserInfo called: userName='$userName'")
            _userState.update { UserState.Loading }
            delay(500)
            postUserInfoUserCase(userName, characterType)
                .onSuccess {
                    Log.d("SetupViewModel", "postUserInfo success, navigating to home")
                    _userState.update { UserState.LoggedIn(UserInfo.EXIST) }
                }
                .onFailure { e ->
                    Log.e("SetupViewModel", "postUserInfo failed: ${e.message}")
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("logoutTest", "SetupViewModel Cleared")
    }
}