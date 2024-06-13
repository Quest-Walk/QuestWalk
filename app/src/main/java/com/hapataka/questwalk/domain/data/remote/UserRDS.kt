package com.hapataka.questwalk.domain.data.remote

import com.hapataka.questwalk.data.dto.UserDTO
import com.hapataka.questwalk.data.model.UserModel

interface UserRDS {
    suspend fun getUserById(userId: String): UserDTO?
    suspend fun uploadUser(user: UserModel)
}