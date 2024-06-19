package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.domain.repository.CacheRepository
import javax.inject.Inject

class SetUserIdToPrefUseCase @Inject constructor(
    private val cacheRepository: CacheRepository
) {
    suspend operator fun invoke(userId: String) {
        cacheRepository.setUserIdToPref(userId)
    }
}