package com.hapataka.questwalk.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hapataka.questwalk.domain.repository.AuthRepository
import com.hapataka.questwalk.domain.repository.LocalRepository
import com.hapataka.questwalk.ui.record.TAG

class LoginViewModel(
    private val authRepo: AuthRepository,
    private val localRepo: LocalRepository
) : ViewModel() {
    private var _userId = MutableLiveData<String>()
    val userId: LiveData<String> get() = _userId

    fun setUserId(id: String) {
        localRepo.setUserId(id)
    }

    fun getUserId() {
        _userId.value = localRepo.getUserId()
    }

}