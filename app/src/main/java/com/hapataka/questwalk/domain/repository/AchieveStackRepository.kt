package com.hapataka.questwalk.domain.repository

import com.hapataka.questwalk.domain.entity.AchieveStackEntity

interface AchieveStackRepository {
    suspend fun countUpAchieveStack(id: Int)
    suspend fun getAchieveStateById(id: Int): AchieveStackEntity
}