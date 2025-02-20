package com.hapataka.questwalk.core.local.api

import com.hapataka.questwalk.core.model.User
import kotlinx.coroutines.flow.Flow

interface LocalUserDataSource {
    fun getCurrentUser(): Flow<User>
    suspend fun existLoginUser(): Boolean
    suspend fun insertUser(user: User)
    suspend fun updateUser(user: User)
    suspend fun clearUsers()
}