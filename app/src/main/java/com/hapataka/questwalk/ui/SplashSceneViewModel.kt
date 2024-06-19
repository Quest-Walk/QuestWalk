package com.hapataka.questwalk.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.data.model.UserModel
import com.hapataka.questwalk.domain.facade.HistoryFacade
import com.hapataka.questwalk.domain.facade.UserFacade
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashSceneViewModel @Inject constructor(
    private val userFacade: UserFacade,
    private val historyFacade: HistoryFacade
): ViewModel() {
    private var _currentUser = MutableLiveData<UserModel> ()
    val currentUser: LiveData<UserModel> = _currentUser
    fun getCurrentUser() {
        viewModelScope.launch {
            delay(2000L)
            _currentUser.value = userFacade.cacheAndGetCurrentUser()
        }
    }

    fun cacheCurrentUserHistories() {
        viewModelScope.launch {
            historyFacade.cacheCurrentUserHistories()
        }
    }
}