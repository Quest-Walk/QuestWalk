package com.hapataka.questwalk.ui.fragment.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.UserRepositoryImpl

import kotlinx.coroutines.launch

class OnBoardingViewModel(private val userRepo: UserRepositoryImpl, private val authRepo: AuthRepositoryImpl) : ViewModel() {

    fun getCurrentUserId(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val userId = authRepo.getCurrentUserUid()
            onResult(userId)
        }
    }
    fun setUserInfo(userId: String, characterNum: Int, nickName: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
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
}

class OnBoardingViewModelFactory(private val userRepo: UserRepositoryImpl,private val authRepo: AuthRepositoryImpl) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OnBoardingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OnBoardingViewModel(userRepo, authRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}