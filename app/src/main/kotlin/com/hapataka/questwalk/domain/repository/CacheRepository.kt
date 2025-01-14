package com.hapataka.questwalk.domain.repository

import com.hapataka.questwalk.core.model.History
import com.hapataka.questwalk.data.model.UserModel
import kotlinx.coroutines.flow.Flow

interface CacheRepository {
    fun getCurrentUser(): UserModel?
    fun cleanCurrentUserCache()
    suspend fun setUserIdToPref(id: String)
    suspend fun getUserIdFromPref(): Flow<String?>
    fun cacheCurrentUser(user: UserModel)
    fun cacheCurrentUserHistories(list: List<History>)
    fun getCurrentUserHistories(): List<History>?
}