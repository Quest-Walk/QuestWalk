package com.hapataka.questwalk.feature.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.common.model.CharacterType
import com.hapataka.questwalk.core.common.model.LoginState
import com.hapataka.questwalk.core.common.model.UserInfo
import com.hapataka.questwalk.core.domain.usecase.PostUserInfoUserCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val postUserInfoUserCase: PostUserInfoUserCase,
) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    fun postUserInfo(userName: String, characterType: CharacterType) {
        viewModelScope.launch {
            postUserInfoUserCase(userName, characterType)
                .onSuccess { _loginState.update { LoginState.Success(UserInfo.EXIST) } }
                .onFailure { e ->
                    Log.e(this.javaClass.name, "fatal: ${e.message}")
                }
        }
    }
}