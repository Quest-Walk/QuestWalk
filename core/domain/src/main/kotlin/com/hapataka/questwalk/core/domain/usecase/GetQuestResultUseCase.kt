package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.HistoryRepository
import com.hapataka.questwalk.core.model.History
import javax.inject.Inject

class GetQuestResultUseCase @Inject constructor(
    private val historyRepository: HistoryRepository,
) {
    suspend operator fun invoke(resultId: String): Result<History.QuestResult> {
        return historyRepository.getQuestResult(resultId)
    }
}