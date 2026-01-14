package com.hapataka.questwalk.core.domain.repository

import com.hapataka.questwalk.core.model.Quest

interface QuestRepository {
    suspend fun getAllQuests(): Result<List<Quest>>
    suspend fun getQuestByKeyword(keyword: String): Result<Quest>
    suspend fun updateQuestSuccess(keyword: String, userId: String, imageUrl: String, registerAt: String): Result<Unit>
}
