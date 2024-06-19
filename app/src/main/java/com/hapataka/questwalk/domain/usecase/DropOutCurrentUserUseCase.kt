package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.domain.repository.AuthRepository
import javax.inject.Inject

class DropOutCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.dropOutCurrentUser()
    }
}