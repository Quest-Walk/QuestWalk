package com.hapataka.questwalk.ui.mainactivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.QuestStackRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.UserRepositoryImpl
import kotlinx.coroutines.launch

class MainViewModel(
    private val authRepo: AuthRepositoryImpl,
    private val userRepo: UserRepositoryImpl,
    private val questRepo: QuestStackRepositoryImpl,
    private val achieveRepo: AuthRepositoryImpl
) : ViewModel() {
    private var _currentUserId = MutableLiveData<String>()
    val currentUserId: LiveData<String>get() = _currentUserId

    fun getCurrentUserId() {
        viewModelScope.launch {
            _currentUserId.value = authRepo.getCurrentUserUid()
        }
    }
}