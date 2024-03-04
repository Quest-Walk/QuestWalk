package com.hapataka.questwalk.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.UserRepositoryImpl
import com.hapataka.questwalk.ui.myinfo.MyInfoViewModel

class ViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val authRepo = AuthRepositoryImpl()
        val userRepo = UserRepositoryImpl()

        if (modelClass.isAssignableFrom(MyInfoViewModel::class.java)) {
            return MyInfoViewModel(authRepo, userRepo) as T
        }
        throw IllegalArgumentException("unknown view model")
    }
}