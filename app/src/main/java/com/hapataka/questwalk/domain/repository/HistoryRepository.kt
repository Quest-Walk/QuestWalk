package com.hapataka.questwalk.domain.repository

import com.hapataka.questwalk.data.model.HistoryModel

interface HistoryRepository {
    suspend fun getUserHistory(userId: String): List<HistoryModel>
}