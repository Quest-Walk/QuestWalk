package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.domain.entity.QuestStackEntity
import com.hapataka.questwalk.domain.repository.QuestStackRepository
import com.hapataka.questwalk.domain.repository.UserRepo
import com.hapataka.questwalk.util.UserInfo
import javax.inject.Inject

class GetAllQuestUseCase @Inject constructor(
    private val questStackRepo: QuestStackRepository,
    private val userRepo: UserRepo,
) {
    suspend operator fun invoke(): List<QuestStackEntity> {
        val allQuestItems = questStackRepo.getAllItems()
        val successQuestItems = userRepo.getResultHistory(UserInfo.uid).filter { it.isSuccess }
        val successKeywords = successQuestItems.map { it.quest }
        val (success, normal) = allQuestItems.partition { successKeywords.contains(it.keyWord) }
        return normal + success
    }
}