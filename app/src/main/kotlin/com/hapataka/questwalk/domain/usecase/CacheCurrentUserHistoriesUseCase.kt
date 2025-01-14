package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.core.domain.repository.HistoryRepository
import com.hapataka.questwalk.domain.repository.CacheRepository
import javax.inject.Inject

class CacheCurrentUserHistoriesUseCase @Inject constructor(
    private val cacheRepository: CacheRepository,
    private val historyRepository: HistoryRepository,
) {
    suspend operator fun invoke() {
        val currentUserId = cacheRepository.getCurrentUser()?.userId ?: return
        val histories = historyRepository.getUserHistory(currentUserId)

        cacheRepository.cacheCurrentUserHistories(histories.getOrThrow())
    }
}