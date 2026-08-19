package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.AuthRepository
import com.hapataka.questwalk.core.domain.repository.HistoryRepository
import com.hapataka.questwalk.core.model.History
import javax.inject.Inject
import javax.inject.Named

class PostHistoryUseCase @Inject constructor(
    @Named("DefaultAuthRepository")
    private val authRepository: AuthRepository,
    private val historyRepository: HistoryRepository,
) {
    suspend operator fun invoke(history: History): Result<String> {
        val userId = authRepository.getUserId()

        return historyRepository.postHistory(userId, history)
    }
}