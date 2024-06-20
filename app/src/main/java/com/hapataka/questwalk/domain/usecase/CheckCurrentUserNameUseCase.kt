package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.domain.repository.CacheRepository
import javax.inject.Inject

class CheckCurrentUserNameUseCase @Inject constructor(
    private val cacheRepository: CacheRepository
) {
    suspend operator fun invoke(): Boolean {
        val userName = cacheRepository.getCurrentUser()?.nickName ?: ""

        return userName.isNotEmpty()
    }
}