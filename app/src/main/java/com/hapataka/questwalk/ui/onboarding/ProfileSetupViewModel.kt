package com.hapataka.questwalk.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.data.repository.backup.AuthRepoImpl
import com.hapataka.questwalk.data.repository.backup.UserRepoImpl

import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileSetupViewModel @Inject constructor(
    private val userRepoImpl: UserRepoImpl,
    private val authRepo: AuthRepoImpl
) : ViewModel() {

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
                userRepoImpl.setUserInfo(userId, characterNum, nickName)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "사용자 정보 저장 중 오류가 발생했습니다.")
            }
        }
    }
}

class OnBoardingViewModelFactory(
    private val userRepoImpl: UserRepoImpl,
    private val authRepo: AuthRepoImpl
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileSetupViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileSetupViewModel(userRepoImpl, authRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}