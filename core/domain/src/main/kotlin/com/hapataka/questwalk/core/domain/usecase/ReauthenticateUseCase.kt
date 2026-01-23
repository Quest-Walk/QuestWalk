package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Named

class ReauthenticateUseCase @Inject constructor(
    @Named("DefaultAuthRepository")
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(password: String): Result<Unit> {
        return authRepository.reauthenticate(password)
    }
}
