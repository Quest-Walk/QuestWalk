package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.QuestRepository
import javax.inject.Inject

class UpdateQuestSuccessUseCase @Inject constructor(
    private val questRepository: QuestRepository,
) {
    suspend operator fun invoke(
        keyword: String,
        userId: String,
        imageUrl: String,
        registerAt: String,
    ): Result<Unit> {
        return questRepository.updateQuestSuccess(keyword, userId, imageUrl, registerAt)
    }
}
