package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.data.model.UserModel
import com.hapataka.questwalk.domain.repository.CacheRepository
import javax.inject.Inject

class GetCacheUserUseCase @Inject constructor(
    private val cacheRepository: CacheRepository
) {
    suspend operator fun invoke(): UserModel? {
        return cacheRepository.getCurrentUser()
    }
}