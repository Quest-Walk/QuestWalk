package com.hapataka.questwalk.domain.data.local

import com.hapataka.questwalk.data.model.UserModel

interface CacheDataSource {
    fun saveUser(user: UserModel)
    fun getUser(): UserModel?
    fun clearUser()

}