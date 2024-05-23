package com.hapataka.questwalk.domain.repository

import com.hapataka.questwalk.data.model.UserModel

interface UserRepository {
    suspend fun getCurrentUser(): UserModel?

}