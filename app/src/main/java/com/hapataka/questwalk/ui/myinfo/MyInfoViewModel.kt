package com.hapataka.questwalk.ui.myinfo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.domain.entity.UserEntity
import com.hapataka.questwalk.domain.repository.AuthRepository
import com.hapataka.questwalk.domain.repository.LocalRepository
import com.hapataka.questwalk.domain.repository.UserRepository
import com.hapataka.questwalk.ui.record.TAG
import kotlinx.coroutines.launch

class MyInfoViewModel(
    private val authRepo: AuthRepository,
    private val userRepo: UserRepository,
    private val localRepo: LocalRepository
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

    fun deleteCurrentUser(pw: String, callback: () -> Unit) {
        viewModelScope.launch {
            val uid = authRepo.getCurrentUserUid()

            Log.i(TAG, "id: ${authRepo.getCurrentUserEmail()}")

            getLoginInfo(pw)
            authRepo.deleteCurrentUser { task ->
                if (task.isSuccessful) {
                    _snackbarMsg.value = "탈퇴 완료"
                    deleteUserData(uid)
                    callback()
                    return@deleteCurrentUser
                } else {
                    Log.i(TAG, "exeption: ${task.exception}")
                    _snackbarMsg.value = "잠시후 다시 시도해주세요"
                    return@deleteCurrentUser
                }
            }
        }
    }

    private suspend fun getLoginInfo(pw: String) {
        val id = authRepo.getCurrentUserEmail()

        authRepo.loginByEmailAndPw(id, pw) {
        }
    }

    private fun deleteUserData(uid: String) {
        viewModelScope.launch {
            userRepo.deleteUserData(uid)
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