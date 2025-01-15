package com.hapataka.questwalk.core.remote.api

import com.hapataka.questwalk.core.model.History

interface HistoryDataSource {
    suspend fun postHistory(userId: String, history: History): Result<String>
    suspend fun getQuestResult(resultId: String): Result<History.QuestResult>
}