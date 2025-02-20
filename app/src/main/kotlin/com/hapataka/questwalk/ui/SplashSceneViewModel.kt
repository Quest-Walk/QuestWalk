package com.hapataka.questwalk.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.domain.usecase.CheckUserLoggedInUseCase
import com.hapataka.questwalk.core.domain.usecase.FetchUserInfoUseCase
import com.hapataka.questwalk.domain.facade.HistoryFacade
import com.hapataka.questwalk.feature.onboarding.model.UserInfo
import com.hapataka.questwalk.feature.onboarding.model.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SplashSceneViewModel @Inject constructor(
    private val historyFacade: HistoryFacade,
    private val fetchUserInfoUseCase: FetchUserInfoUseCase,
    private val checkUserLoggedInUseCase: CheckUserLoggedInUseCase,
) : ViewModel() {
    private val _loginState = MutableStateFlow<UserState>(UserState.Loading)
    val loginState = _loginState.asStateFlow()

    init {
        checkLoggedIn()
    }

    private fun checkLoggedIn() {
        viewModelScope.launch {
            if (checkUserLoggedInUseCase()) {
                checkUserInfo()
            } else {
                _loginState.update { UserState.LoggedOut }
            }
        }
    }

    private fun checkUserInfo() {
        viewModelScope.launch {
            fetchUserInfoUseCase()
                .onSuccess { _loginState.update { UserState.LoggedIn(UserInfo.EXIST) } }
                .onFailure { _loginState.update { UserState.LoggedIn(UserInfo.NONE) } }
        }
    }

    suspend fun cacheCurrentUserHistories() {
        withContext(Dispatchers.IO) {
            historyFacade.cacheCurrentUserHistories()
        }
    }
}