package com.hapataka.questwalk.core.remote.api

import com.hapataka.questwalk.core.remote.model.QuestDto

interface QuestDataSource {
    suspend fun getAllQuests(): List<QuestDto>
    suspend fun getQuestByKeyword(keyword: String): QuestDto?
    suspend fun updateQuestSuccess(keyword: String, userId: String, imageUrl: String, registerAt: String)
}
