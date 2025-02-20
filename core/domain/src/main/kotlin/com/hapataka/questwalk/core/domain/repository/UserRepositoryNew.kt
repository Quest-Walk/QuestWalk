package com.hapataka.questwalk.core.domain.repository

import com.hapataka.questwalk.core.model.CharacterType
import com.hapataka.questwalk.core.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepositoryNew {
    suspend fun checkUserLoggedIn(): Boolean
    suspend fun insertUser(userId: String)
    suspend fun clearUserInfo()
    fun getUserInfo(): Flow<User>
    suspend fun fetchUserInfo(): Result<Unit>
    suspend fun postUserInfo(
        userId: String,
        userName: String,
        characterType: CharacterType,
    ): Result<Unit>

    suspend fun updateUserInfo(
        time: Long,
        distance: Float,
        step: Long,
        keyword: String,
        achievementId: Int? = null
    )
}