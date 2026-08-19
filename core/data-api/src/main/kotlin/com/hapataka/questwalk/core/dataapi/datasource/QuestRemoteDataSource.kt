package com.hapataka.questwalk.core.dataapi.datasource

import com.hapataka.questwalk.core.dataapi.model.QuestDto
import com.hapataka.questwalk.core.dataapi.model.SuccessItemDto

interface QuestRemoteDataSource {
    suspend fun getAllQuests(): Result<List<QuestDto>>
    suspend fun getQuestByKeyword(keyword: String): Result<QuestDto?>
    suspend fun updateQuestSuccess(keyword: String, successItem: SuccessItemDto): Result<Unit>
}
