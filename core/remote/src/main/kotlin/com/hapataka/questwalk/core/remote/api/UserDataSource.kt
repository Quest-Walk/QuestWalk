package com.hapataka.questwalk.core.remote.api

import com.hapataka.questwalk.core.model.User

interface UserDataSource {
    suspend fun getUserInfo(userId: String): Result<User>
}