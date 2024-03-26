package com.hapataka.questwalk.domain.usecase

import android.util.Log
import com.hapataka.questwalk.domain.entity.HistoryEntity
import com.hapataka.questwalk.domain.entity.QuestStackEntity
import com.hapataka.questwalk.domain.repository.AuthRepository
import com.hapataka.questwalk.domain.repository.QuestStackRepository
import com.hapataka.questwalk.domain.repository.UserRepository
import com.hapataka.questwalk.util.UserInfo

class GetAllQuestUseCase(
    private val questStackRepo: QuestStackRepository,
    private val userRepo: UserRepository,
) {
    suspend operator fun invoke(): List<QuestStackEntity> {
        val allQuestItems = questStackRepo.getAllItems()
        val successQuestItems = userRepo.getResultHistory(UserInfo.uid).filter { it.isSuccess }
        val successKeywords = successQuestItems.map { it.quest }
        val (success, normal) = allQuestItems.partition { successKeywords.contains(it.keyWord) }
        return normal + success
    }
}