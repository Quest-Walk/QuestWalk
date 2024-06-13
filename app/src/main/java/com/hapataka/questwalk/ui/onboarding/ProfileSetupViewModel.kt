package com.hapataka.questwalk.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.domain.facade.UserFacade
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileSetupViewModel @Inject constructor(
    private val userFacade: UserFacade,
) : ViewModel() {
    fun updateUserName(newName: String) {
        viewModelScope.launch {
            userFacade.updateUserName(newName)
        }
    }
//
//    fun getCurrentUserId(onResult: (String) -> Unit) {
//        viewModelScope.launch {
//            val userId = authRepo.getCurrentUserUid()
//            onResult(userId)
//        }
//    }
//
//    fun setUserInfo(
//        userId: String,
//        characterNum: Int,
//        nickName: String,
//        onSuccess: () -> Unit,
//        onError: (String) -> Unit
//    ) {
//        if (userId.isEmpty()) {
//            onError("사용자가 로그인하지 않았습니다.")
//            return
//        }
//
//        viewModelScope.launch {
//            try {
//                userRepoImpl.setUserInfo(userId, characterNum, nickName)
//                onSuccess()
//            } catch (e: Exception) {
//                onError(e.message ?: "사용자 정보 저장 중 오류가 발생했습니다.")
//            }
//        }
//    }
}