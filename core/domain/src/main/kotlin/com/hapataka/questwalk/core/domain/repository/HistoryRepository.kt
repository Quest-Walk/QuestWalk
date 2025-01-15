package com.hapataka.questwalk.core.domain.repository

import com.hapataka.questwalk.core.model.History

interface HistoryRepository {
    suspend fun getUserHistories(userId: String): Result<List<History>>
    suspend fun postHistory(userId: String, history: History): Result<String>
    suspend fun deleteHistoriesById(userId: String): Result<Unit>
    suspend fun getQuestResult(resultId: String): Result<History.QuestResult>
}