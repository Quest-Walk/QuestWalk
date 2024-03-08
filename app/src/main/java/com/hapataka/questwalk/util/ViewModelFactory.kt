package com.hapataka.questwalk.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hapataka.questwalk.data.firebase.repository.AuthRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.ImageRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.QuestStackRepositoryImpl
import com.hapataka.questwalk.data.firebase.repository.UserRepositoryImpl
import com.hapataka.questwalk.ui.home.HomeViewModel
import com.hapataka.questwalk.ui.login.LoginViewModel
import com.hapataka.questwalk.ui.mainactivity.MainViewModel
import com.hapataka.questwalk.ui.myinfo.MyInfoViewModel
import com.hapataka.questwalk.ui.record.RecordViewModel
import com.hapataka.questwalk.ui.result.ResultViewModel

class ViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val authRepo = AuthRepositoryImpl()
        val userRepo = UserRepositoryImpl()
        val questRepo = QuestStackRepositoryImpl()
        val achieveRepo = AuthRepositoryImpl()
        val imageRepo = ImageRepositoryImpl()

        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(authRepo, userRepo, questRepo, achieveRepo) as T
        }

        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(authRepo) as T
        }

        if (modelClass.isAssignableFrom(MyInfoViewModel::class.java)) {
            return MyInfoViewModel(authRepo, userRepo) as T
        }

        if (modelClass.isAssignableFrom(RecordViewModel::class.java)) {
            return RecordViewModel(authRepo, userRepo) as T
        }

        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(authRepo, userRepo, imageRepo, questRepo) as T
        }

        if (modelClass.isAssignableFrom(ResultViewModel::class.java)) {
            return ResultViewModel(userRepo, questRepo) as T
        }

        throw IllegalArgumentException("unknown view model")
    }
}