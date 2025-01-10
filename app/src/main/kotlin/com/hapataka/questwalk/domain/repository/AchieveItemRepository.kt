package com.hapataka.questwalk.domain.repository

import com.hapataka.questwalk.domain.entity.AchieveItemEntity

interface AchieveItemRepository {
    suspend fun getAchieveItem(): List<AchieveItemEntity>
}