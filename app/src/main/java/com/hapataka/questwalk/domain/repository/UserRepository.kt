package com.hapataka.questwalk.domain.repository

import com.hapataka.questwalk.data.model.UserModel
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserById(id: String): UserModel?
    suspend fun cacheUser(user: UserModel)
    suspend fun uploadUser(user: UserModel)
    suspend fun getCachedUser(): UserModel?
    suspend fun getUserIdFromPref(): Flow<String?>
    suspend fun setUserIdToPref(id: String)
    suspend fun clearCachedUser()
}