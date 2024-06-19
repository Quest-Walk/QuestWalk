package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.domain.repository.CacheRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserIdFromPrefUseCase @Inject constructor(
    private val cacheRepository: CacheRepository
) {
    suspend operator fun invoke(): Flow<String?> {
        return cacheRepository.getUserIdFromPref()
    }

}