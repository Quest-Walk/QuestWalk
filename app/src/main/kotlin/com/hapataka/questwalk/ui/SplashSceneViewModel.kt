package com.hapataka.questwalk.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.domain.usecase.GetLoginUserIdUseCase
import com.hapataka.questwalk.core.domain.usecase.GetUserInfoUseCase
import com.hapataka.questwalk.core.model.LoginState
import com.hapataka.questwalk.core.model.UserInfo
import com.hapataka.questwalk.data.model.UserModel
import com.hapataka.questwalk.domain.facade.HistoryFacade
import com.hapataka.questwalk.domain.facade.UserFacade
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SplashSceneViewModel @Inject constructor(
    private val userFacade: UserFacade,
    private val historyFacade: HistoryFacade,
    private val getLoginUserIdUseCase: GetLoginUserIdUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
) : ViewModel() {
    private var _currentUser = MutableLiveData<UserModel>()
    val currentUser: LiveData<UserModel> = _currentUser

    private val _userState = MutableStateFlow<LoginState>(LoginState.Loading)
    val userState: StateFlow<LoginState> = _userState

    init {
        checkUserState()
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = userFacade.cacheAndGetCurrentUser()
        }
    }

    private fun checkUserState() {
        viewModelScope.launch {
            getLoginUserIdUseCase()
                .onSuccess { userId -> checkUserInfo(userId) }
                .onFailure { e ->
                    Log.e(this.javaClass.name, "Fatal: ${e.message}")
                    _userState.update { LoginState.Failure("로그인 정보가 없습니다.") }
                }
        }
    }

    private fun checkUserInfo(userId: String) {
        viewModelScope.launch {
            getUserInfoUseCase(userId)
                .onSuccess {
                    _userState.update { LoginState.Success(UserInfo.EXIST) }
                }
                .onFailure {
                    _userState.update { LoginState.Success(UserInfo.NONE) }
                }
        }
    }

    suspend fun cacheCurrentUserHistories() {
        withContext(Dispatchers.IO) {
            historyFacade.cacheCurrentUserHistories()
        }
    }
}