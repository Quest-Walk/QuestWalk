package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.HistoryRepository
import com.hapataka.questwalk.core.model.History
import javax.inject.Inject

class GetUserHistoriesUseCase @Inject constructor(
    private val historyRepository: HistoryRepository,
) {
    suspend operator fun invoke(userId: String): Result<List<History>> {
        return historyRepository.getUserHistories(userId)
    }
}
