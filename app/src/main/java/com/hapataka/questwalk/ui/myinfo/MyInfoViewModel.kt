package com.hapataka.questwalk.ui.myinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.data.model.UserModel
import com.hapataka.questwalk.domain.entity.UserEntity
import com.hapataka.questwalk.domain.facade.AuthFacade
import com.hapataka.questwalk.domain.facade.HistoryFacade
import com.hapataka.questwalk.domain.facade.UserFacade
import com.hapataka.questwalk.domain.repository.AuthRepo
import com.hapataka.questwalk.domain.repository.UserRepo
import com.hapataka.questwalk.util.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyInfoViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val userRepo: UserRepo,
    private val userFacade: UserFacade,
    private val authFacade: AuthFacade,
    private val historyFacade: HistoryFacade
) : ViewModel() {
    private var _currentUser = MutableLiveData<UserModel>()
    val currentUser: LiveData<UserModel> get() = _currentUser

    private var _logoutSuccess = MutableLiveData<Boolean>(false)
    val logoutSuccess: LiveData<Boolean> get() = _logoutSuccess

    private var _userInfo = MutableLiveData<UserEntity>()
    val userInfo: LiveData<UserEntity> get() = _userInfo

    private var _toastMsg = MutableLiveData<String>()
    val toastMsg: LiveData<String> get() = _toastMsg

    private var _historyCount = MutableLiveData<Map<Int, Int>> ()
    val historyCount: LiveData<Map<Int, Int>> get() = _historyCount

    private var _btnState = MutableLiveData<Boolean>(false)
    val btnState: LiveData<Boolean> get() = _btnState

    fun getCurrentUserInfo() {
        viewModelScope.launch {
            userFacade.getCacheUser()?.let {
                _currentUser.value = it
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _btnState.value = false
            val result = authFacade.logout()

            if (result.isSuccess) {
                _logoutSuccess.value = true
                delay(500L)
                _toastMsg.value = "로그아웃 완료!"

            } else {
                _logoutSuccess.value = false
                _toastMsg.value = "잠시후 다시 시도해주세요"
            }
            _btnState.value = true
        }
    }

    fun getHistoryCount() {
        _historyCount.value = historyFacade.countCurrentUserHistories()
    }

    fun deleteCurrentUser(callback: () -> Unit) {
        viewModelScope.launch {

            authRepo.deleteCurrentUser() { task ->
                if (task.isSuccessful) {
                    _toastMsg.value = "탈퇴 완료"
                    deleteUserData()
                    callback()
                    return@deleteCurrentUser
                } else {
                    _toastMsg.value = "잠시후 다시 시도해주세요"
                    return@deleteCurrentUser
                }
            }
        }
    }

    fun changeUserNickName(newNickName: String) {
        viewModelScope.launch {
            userFacade.updateUserName(newNickName)
        }
    }

    fun reauthCurrentUser(pw: String, positiveCallback: () -> Unit, negativeCallback: () -> Unit) {
        viewModelScope.launch {
            val result = authRepo.reauth(pw)

            if (result) {
                positiveCallback()
            } else {
                negativeCallback()
            }
        }
    }

    private fun deleteUserData() {
        viewModelScope.launch {
            userRepo.deleteUserData(UserInfo.uid)
        }
    }

    fun getCurrentUserId(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val userId = authRepo.getCurrentUserUid()
            onResult(userId)
        }
    }


    fun setUserInfo(
        userId: String,
        characterNum: Int,
        nickName: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (userId.isEmpty()) {
            onError("사용자가 로그인하지 않았습니다.")
            return
        }
        viewModelScope.launch {
            try {
                userRepo.setUserInfo(userId, characterNum, nickName)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "사용자 정보 저장 중 오류가 발생했습니다.")
            }
        }
    }

    fun getUserCharacterNum(onResult: (Int?) -> Unit) {
        val characterId = _userInfo.value?.characterId
        onResult(characterId)
    }
}