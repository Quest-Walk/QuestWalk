package com.hapataka.questwalk.domain.data.remote

import com.hapataka.questwalk.data.dto.HistoryResultDTO

interface HistoryRDS {
    suspend fun getHistoriesById(id: String): HistoryResultDTO
    suspend fun deleteHistoriesById(id: String): Result<Unit>
    suspend fun uploadHistoryInfo(history: Any)
}