package com.hapataka.questwalk.domain.repository

import com.hapataka.questwalk.data.model.HistoryModel
import com.hapataka.questwalk.data.model.UserModel
import kotlinx.coroutines.flow.Flow

interface CacheRepository {
    fun getCurrentUser(): UserModel?
    fun cleanCurrentUserCache()
    suspend fun setUserIdToPref(id: String)
    suspend fun getUserIdFromPref(): Flow<String?>
    fun cacheCurrentUser(user: UserModel)
    fun caheCurrentUserHistories(list: List<HistoryModel>)
    fun getCurrentUserHistories(): List<HistoryModel>?
}