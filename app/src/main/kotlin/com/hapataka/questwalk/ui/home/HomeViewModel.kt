package com.hapataka.questwalk.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.domain.data.remote.EncryptionKeyRepository
import com.hapataka.questwalk.domain.repository.AuthRepo
import com.hapataka.questwalk.util.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val encryptRepo: EncryptionKeyRepository,
) : ViewModel() {
    private val _timeState = MutableStateFlow(-1)
    val timeState = _timeState.asStateFlow()

    private var _isNight = MutableLiveData(false)

    init {
        viewModelScope.launch {
            while (true) {
                _timeState.update { LocalTime.now().hour }
                delay(1000L)
            }
        }
    }

    suspend fun setUserInfo() {
        UserInfo.uid = authRepo.getCurrentUserUid()
        UserInfo.encryptionKey = encryptRepo.getKey(UserInfo.uid)
    }
}