package com.hapataka.questwalk.core.dataapi.datasource

import com.hapataka.questwalk.core.dataapi.model.HistoryDto

interface HistoryRemoteDataSource {
    suspend fun getUserHistories(userId: String): Result<List<HistoryDto>>
    suspend fun postHistory(historyDto: HistoryDto): Result<String>
    suspend fun getQuestResult(resultId: String): Result<HistoryDto.QuestResultDto>
    suspend fun deleteHistoriesByUserId(userId: String): Result<Unit>
}
