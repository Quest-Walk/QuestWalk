package com.hapataka.questwalk.core.dataapi.datasource

import com.hapataka.questwalk.core.dataapi.model.AchieveItemDto

interface AchieveItemRemoteDataSource {
    suspend fun getAchieveItems(): Result<List<AchieveItemDto>>
}
