package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.domain.repository.AuthRepository
import com.hapataka.questwalk.domain.repository.CacheRepository
import com.hapataka.questwalk.domain.repository.HistoryRepository
import javax.inject.Inject

class CacheCurrentUserHistoriesUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val cacheRepository: CacheRepository,
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke() {
        val currentUserId = authRepository.getCurrentUserId() ?: return
        val histories = historyRepository.getUserHistory(currentUserId)

        cacheRepository.caheCurrentUserHistories(histories)
    }
}