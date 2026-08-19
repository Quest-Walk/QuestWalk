package com.hapataka.questwalk.core.remote.api

import com.hapataka.questwalk.core.remote.model.AchieveItemDto

interface AchieveItemDataSource {
    suspend fun getAchieveItems(): List<AchieveItemDto>
}
