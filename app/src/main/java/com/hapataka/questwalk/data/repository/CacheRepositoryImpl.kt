package com.hapataka.questwalk.data.repository

import com.hapataka.questwalk.data.model.HistoryModel
import com.hapataka.questwalk.data.model.UserModel
import com.hapataka.questwalk.domain.data.local.CacheDataSource
import com.hapataka.questwalk.domain.repository.CacheRepository
import com.hapataka.questwalk.domain.repository.PrefDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CacheRepositoryImpl @Inject constructor(
    private val cacheDataSource: CacheDataSource,
    private val prefDataSource: PrefDataSource
): CacheRepository {
    override fun cacheCurrentUser(user: UserModel) {
        cacheDataSource.setCurrentUser(user)
    }

    override fun getCurrentUser(): UserModel? {
        return cacheDataSource.getCurrentUser()
    }

    override fun cleanCurrentUserCache() {
        cacheDataSource.clearCurrentUserInfo()
    }

    override fun caheCurrentUserHistories(histories: List<HistoryModel>) {
        cacheDataSource.setCurrentUserHistories(histories)
    }

    override fun getCurrentUserHistories(): List<HistoryModel>? {
        return cacheDataSource.getCurrentUserHistories()
    }

    override suspend fun setUserIdToPref(id: String) {
        prefDataSource.setUserId(id)
    }
    override suspend fun getUserIdFromPref(): Flow<String?> {
        return prefDataSource.getUserId()
    }
}