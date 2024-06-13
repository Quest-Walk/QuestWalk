package com.hapataka.questwalk.domain.data.remote

import com.hapataka.questwalk.data.dto.HistoryResultDTO

interface HistoryRDS {
    suspend fun getHistoriesById(id: String): HistoryResultDTO
}