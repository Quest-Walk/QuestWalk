package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.domain.repository.AuthRepository
import javax.inject.Inject

class ReauthCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(pw: String): Result<Unit> {
        return authRepository.reauthCurrentUser(pw)
    }
}