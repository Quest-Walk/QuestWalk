package com.hapataka.questwalk.domain.repository

import com.hapataka.questwalk.domain.entity.QuestStackEntity

interface QuestStackRepository {
    suspend fun updateQuest(keyword: String, userId: String, imageUrl: String, registerAt: String)
    suspend fun getItemByKeyword(keyword: String): QuestStackEntity
    suspend fun getAllItems(): List<QuestStackEntity>
}