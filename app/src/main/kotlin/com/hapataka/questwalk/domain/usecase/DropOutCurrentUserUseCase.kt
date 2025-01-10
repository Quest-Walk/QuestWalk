package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.domain.repository.PrevAuthRepository
import javax.inject.Inject

class DropOutCurrentUserUseCase @Inject constructor(
    private val authRepository: PrevAuthRepository,
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.dropOutCurrentUser()
    }
}