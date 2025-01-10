package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.domain.repository.PrevAuthRepository
import javax.inject.Inject

class ReauthCurrentUserUseCase @Inject constructor(
    private val authRepository: PrevAuthRepository,
) {
    suspend operator fun invoke(pw: String): Result<Unit> {
        return authRepository.reauthCurrentUser(pw)
    }
}