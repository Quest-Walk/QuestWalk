package com.hapataka.questwalk.core.domain.repository

import com.hapataka.questwalk.core.common.model.CharacterType
import com.hapataka.questwalk.core.model.User

interface UserRepositoryNew {
    suspend fun getUserInfo(userId: String): Result<User>
    suspend fun postUserInfo(
        userId: String,
        userName: String,
        characterType: CharacterType,
    ): Result<Unit>
}