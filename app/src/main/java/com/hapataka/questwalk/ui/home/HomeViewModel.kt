package com.hapataka.questwalk.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.domain.repository.AuthRepository
import com.hapataka.questwalk.domain.repository.ImageRepository
import com.hapataka.questwalk.domain.repository.QuestStackRepository
import com.hapataka.questwalk.domain.repository.UserRepository
import kotlinx.coroutines.launch
import java.time.LocalTime

class HomeViewModel(
    private val authRepo: AuthRepository,
    private val userRepo: UserRepository,
    private val imageRepo: ImageRepository,
    private val questRepo: QuestStackRepository
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

    private fun getUserCharNum() {
        viewModelScope.launch {
            val userId = authRepo.getCurrentUserUid()

            val userInfo = userRepo.getInfo(userId)
            _charNum.value = userInfo.characterId
        }
    }

}