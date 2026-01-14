package com.hapataka.questwalk.core.domain.repository

import com.hapataka.questwalk.core.model.AchieveItem

interface AchieveItemRepository {
    suspend fun getAchieveItems(): Result<List<AchieveItem>>
}
