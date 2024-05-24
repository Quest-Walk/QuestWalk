package com.hapataka.questwalk.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hapataka.questwalk.domain.repository.AuthRepo
import com.hapataka.questwalk.domain.data.remote.EncryptionKeyRepository
import com.hapataka.questwalk.domain.repository.UserRepo
import com.hapataka.questwalk.util.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val userRepo: UserRepo,
    private val encryptRepo: EncryptionKeyRepository
) : ViewModel() {
    private var _isNight = MutableLiveData(false)
    val isNight: LiveData<Boolean> get() = _isNight

    private var _charNum = MutableLiveData<Int>()
    private var time = -1

    fun checkCurrentTime() {
        time = LocalTime.now().hour

        when (time) {
            in 7..18 -> _isNight.value = false
            else -> _isNight.value = true
        }
    }

    suspend fun setUserInfo() {
        UserInfo.uid = authRepo.getCurrentUserUid()
        UserInfo.encryptionKey = encryptRepo.getKey(UserInfo.uid)
    }
}