package com.hapataka.questwalk.domain.repository

import com.hapataka.questwalk.data.model.UserModel

interface UserRepository {
    suspend fun getUserById(id: String): UserModel?
    suspend fun uploadUser(user: UserModel)
}