package com.hapataka.questwalk.domain.data.remote

import com.hapataka.questwalk.core.remote.model.UserDto
import com.hapataka.questwalk.data.model.UserModel

interface UserRDS {
    suspend fun getUserById(userId: String): UserDto?
    suspend fun uploadUser(user: UserModel)
    suspend fun deleteUserById(userId: String)
}