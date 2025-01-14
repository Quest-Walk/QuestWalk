package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.core.model.History
import com.hapataka.questwalk.domain.repository.CacheRepository
import javax.inject.Inject

class GetCurrentUserHistoriesUseCase @Inject constructor(
    private val cacheRepository: CacheRepository,
) {
    operator fun invoke(): List<History>? {
        return cacheRepository.getCurrentUserHistories()
    }
}