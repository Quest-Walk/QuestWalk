package com.hapataka.questwalk.ui.myinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.UserRepositoryImpl
import com.hapataka.questwalk.domain.entity.UserEntity
import kotlinx.coroutines.launch

class MyInfoViewModel(
    private val authRepo: AuthRepositoryImpl,
    private val userRepo: UserRepositoryImpl
) : ViewModel() {
    private var _userInfo = MutableLiveData<UserEntity>()
    val userInfo: LiveData<UserEntity> get() = _userInfo
    private var _snackbarMsg = MutableLiveData<String>()
    val snackbarMsg: LiveData<String> get() = _snackbarMsg

    fun getUserInfo() {
        viewModelScope.launch {
            _userInfo.value = userRepo.getInfo(authRepo.getCurrentUserUid())
        }
    }

    fun logout(callback: () -> Unit) {
        viewModelScope.launch {
            authRepo.logout()
            _snackbarMsg.value = "로그아웃 완료"
            callback()
        }
    }

    fun leaveCurrentUser(callback: () -> Unit) {
        viewModelScope.launch {
            authRepo.deleteCurrentUser { task ->
                if (task.isSuccessful) {
                    _snackbarMsg.value = "탈퇴 완료"
                    callback()
                }
            }
        }
    }
}