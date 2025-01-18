package com.hapataka.questwalk.feature.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.common.model.CharacterType
import com.hapataka.questwalk.core.common.model.UserInfo
import com.hapataka.questwalk.core.common.model.UserState
import com.hapataka.questwalk.core.domain.usecase.LogoutUseCase
import com.hapataka.questwalk.core.domain.usecase.PostUserInfoUserCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val postUserInfoUserCase: PostUserInfoUserCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {
    private val _userState = MutableStateFlow<UserState>(UserState.Idle)
    val loginState = _userState.asStateFlow()

    fun postUserInfo(userName: String, characterType: CharacterType) {
        viewModelScope.launch {
            _userState.update { UserState.Loading }
            delay(500)
            postUserInfoUserCase(userName, characterType)
                .onSuccess { _userState.update { UserState.LoggedIn(UserInfo.EXIST) } }
                .onFailure { e ->
                    Log.e(this.javaClass.name, "fatal: ${e.message}")
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}