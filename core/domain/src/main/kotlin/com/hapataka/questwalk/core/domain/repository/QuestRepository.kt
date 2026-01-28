package com.hapataka.questwalk.core.domain.repository

import com.hapataka.questwalk.core.model.Quest
import kotlinx.coroutines.flow.StateFlow

interface QuestRepository {
    val currentQuest: StateFlow<Quest?>

    suspend fun getAllQuests(): Result<List<Quest>>
    suspend fun getQuestByKeyword(keyword: String): Result<Quest>
    suspend fun updateQuestSuccess(keyword: String, userId: String, imageUrl: String, registerAt: String): Result<Unit>

    fun setCurrentQuest(quest: Quest)
    fun clearCurrentQuest()
}
