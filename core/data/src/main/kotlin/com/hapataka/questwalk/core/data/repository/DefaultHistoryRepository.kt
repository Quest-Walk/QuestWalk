package com.hapataka.questwalk.core.data.repository

import com.hapataka.questwalk.core.domain.repository.HistoryRepository
import com.hapataka.questwalk.core.model.History
import com.hapataka.questwalk.core.remote.api.HistoryDataSource
import javax.inject.Inject
import javax.inject.Named

class DefaultHistoryRepository @Inject constructor(
    @Named("FirestoreHistory")
    private val historyDataSource: HistoryDataSource,
) : HistoryRepository {
    override suspend fun getUserHistory(userId: String): Result<List<History>> {
        TODO("Not yet implemented")
    }

    override suspend fun postHistory(userId: String, history: History): Result<Unit> {
        return historyDataSource.postHistory(userId, history)
    }

    override suspend fun deleteHistoriesById(userId: String): Result<Unit> {
        TODO("Not yet implemented")
    }
}