package com.hapataka.questwalk.core.domain.repository

import com.hapataka.questwalk.core.model.History

interface HistoryRepository {
    suspend fun getUserHistory(userId: String): Result<List<History>>
    suspend fun postHistory(userId: String, history: History): Result<Unit>
    suspend fun deleteHistoriesById(userId: String): Result<Unit>
}