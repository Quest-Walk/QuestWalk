package com.hapataka.questwalk.ui.myinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.core.domain.usecase.FetchUserInfoUseCase
import com.hapataka.questwalk.core.domain.usecase.GetUserInfoUseCase
import com.hapataka.questwalk.core.domain.usecase.LogoutUseCase
import com.hapataka.questwalk.core.model.User
import com.hapataka.questwalk.data.model.UserModel
import com.hapataka.questwalk.domain.facade.AuthFacade
import com.hapataka.questwalk.domain.facade.HistoryFacade
import com.hapataka.questwalk.domain.facade.UserFacade
import com.hapataka.questwalk.ui.result.model.UiState
import com.hapataka.questwalk.util.extentions.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyInfoViewModel @Inject constructor(
    private val userFacade: UserFacade,
    private val authFacade: AuthFacade,
    private val historyFacade: HistoryFacade,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val fetchUserInfoUseCase: FetchUserInfoUseCase,
) : ViewModel() {
    private val _user = MutableStateFlow<UiState<User>>(UiState.Idle)
    val user = _user.asStateFlow()

    private var _currentUser = MutableLiveData<UserModel>()
    val currentUser: LiveData<UserModel> get() = _currentUser

    private var _logoutSuccess = MutableLiveData<Boolean>(false)
    val logoutSuccess: LiveData<Boolean> get() = _logoutSuccess

    private var _reauthSuccess = MutableLiveData<Boolean>(false)
    val reauthSuccess: LiveData<Boolean> get() = _reauthSuccess

    private var _toastMsg = MutableLiveData<String>()
    val toastMsg: LiveData<String> get() = _toastMsg

    private var _historyCount = MutableLiveData<Map<Int, Int>>()
    val historyCount: LiveData<Map<Int, Int>> get() = _historyCount

    private var _btnState = MutableLiveData<Boolean>(false)
    val btnState: LiveData<Boolean> get() = _btnState

    private var _dropOutSuccess = MutableLiveData<Boolean>(false)
    val dropOutSuccess: LiveData<Boolean> get() = _dropOutSuccess

    init {
        viewModelScope.launch {
            fetchUserInfoUseCase()
            getUserInfoUseCase()
                .onStart { _user.update { UiState.Loading } }
                .catch { e -> _user.update { UiState.Failure(e) } }
                .first().let { userInfo -> _user.update { UiState.Success(userInfo) } }

        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _toastMsg.value = "로그아웃 완료!"
        }
    }

    fun getHistoryCount() {
        _historyCount.value = historyFacade.countCurrentUserHistories()
    }

    fun reauthCurrentUser(pw: String) {
        viewModelScope.launch {
            val result = authFacade.reauthCurrentUser(pw)

            if (result.isSuccess) {
                _reauthSuccess.value = true
            } else {
                val exception = result.exceptionOrNull() ?: return@launch

                _toastMsg.value = exception.getErrorMessage()
                _reauthSuccess.value = false
            }
        }
    }

    fun dropOutCurrentUser() {
        viewModelScope.launch {
            val result = authFacade.dropOutCurrentUser()

            if (result.isSuccess) {
                _dropOutSuccess.value = true
            } else {
                val exception = result.exceptionOrNull() ?: return@launch

                _dropOutSuccess.value = false
                _toastMsg.value = exception.getErrorMessage()
            }
        }
    }

    fun changeUserNickName(newNickName: String) {
        viewModelScope.launch {
            userFacade.updateUserName(newNickName)
        }
    }
}