package com.hapataka.questwalk.data.datasource.local

import com.hapataka.questwalk.data.model.UserModel
import com.hapataka.questwalk.domain.data.local.CacheDataSource
import javax.inject.Inject

class CacheDataSourceImpl @Inject constructor() : CacheDataSource {
    private var user: UserModel? = null
    override fun saveUser(user: UserModel) {
        this.user = user
    }

    override fun getUser(): UserModel? {
        return if (user == null) null else user
    }

    override fun clearUser() {
        user = null
    }
}