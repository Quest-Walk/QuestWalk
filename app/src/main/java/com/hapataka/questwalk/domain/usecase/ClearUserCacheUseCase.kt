package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.domain.repository.UserRepository
import javax.inject.Inject

class ClearUserCacheUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        userRepository.clearCachedUser()
    }
}