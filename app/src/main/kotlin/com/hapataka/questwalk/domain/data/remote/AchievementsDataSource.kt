package com.hapataka.questwalk.domain.data.remote

import com.hapataka.questwalk.data.dto.AchievementItemDTO

interface AchievementsDataSource {
    suspend fun getAchievements(): List<AchievementItemDTO>
}