package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        authRepository.getCurrentUserInfo()
    }
}