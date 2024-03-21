package com.hapataka.questwalk.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hapataka.questwalk.domain.repository.AuthRepository
import com.hapataka.questwalk.util.UserInfo
import java.time.LocalTime

class HomeViewModel(
    private val authRepo: AuthRepository
) : ViewModel() {
    private var _isNight = MutableLiveData(false)
    val isNight: LiveData<Boolean> get() = _isNight

    private var time = -1

    fun checkCurrentTime() {
        time = LocalTime.now().hour

        when (time) {
            in 7..18 -> _isNight.value = false
            else -> _isNight.value = true
        }
    }

    suspend fun setUid() {
        UserInfo.uid = authRepo.getCurrentUserUid()
    }
}