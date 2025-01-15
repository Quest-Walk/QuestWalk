package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.HistoryRepository
import com.hapataka.questwalk.core.model.History
import javax.inject.Inject
import javax.inject.Named

class GetQuestResultUseCase @Inject constructor(
    @Named("DefaultHistoryRepository")
    private val historyRepository: HistoryRepository,
) {
    suspend operator fun invoke(resultId: String): Result<History.QuestResult> {
        return historyRepository.getQuestResult(resultId)
    }
}