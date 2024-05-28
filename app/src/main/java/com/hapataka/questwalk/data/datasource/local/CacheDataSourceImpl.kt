package com.hapataka.questwalk.data.datasource.local

import android.util.Log
import com.hapataka.questwalk.data.model.UserModel
import com.hapataka.questwalk.domain.data.local.CacheDataSource
import javax.inject.Inject

class CacheDataSourceImpl @Inject constructor() : CacheDataSource {
    private var user: UserModel? = null
    override fun saveUser(user: UserModel) {
        this.user = user
        Log.e("down_user_test", "save: ${this.user}")
    }

    override fun getUser(): UserModel? {
        Log.i("down_user_test", "get: $user")
        return if (user == null) null else user
    }

    override fun clearUser() {
        user = null
    }
}