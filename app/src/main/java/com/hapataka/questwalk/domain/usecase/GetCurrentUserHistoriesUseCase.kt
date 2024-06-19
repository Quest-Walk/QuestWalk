package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.data.model.HistoryModel
import com.hapataka.questwalk.domain.repository.CacheRepository
import javax.inject.Inject

class GetCurrentUserHistoriesUseCase @Inject constructor(
    private val cacheRepository: CacheRepository
) {
    operator fun invoke(): List<HistoryModel>? {
        return cacheRepository.getCurrentUserHistories()
    }
}