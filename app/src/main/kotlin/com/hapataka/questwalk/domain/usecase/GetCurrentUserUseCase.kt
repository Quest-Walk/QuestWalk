package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.domain.repository.PrevAuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: PrevAuthRepository,
) {
    suspend operator fun invoke() {
        authRepository.getCurrentUserId()
    }
}