package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.domain.repository.CacheRepository
import javax.inject.Inject

class ClearUserCacheUseCase @Inject constructor(
    private val cacheRepository: CacheRepository
) {
    suspend operator fun invoke() {
        cacheRepository.cleanCurrentUserCache()
    }
}