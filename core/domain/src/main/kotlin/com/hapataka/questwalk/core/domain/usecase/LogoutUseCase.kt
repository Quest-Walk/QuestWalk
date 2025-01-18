package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Named

class LogoutUseCase @Inject constructor(
    @Named("DefaultAuthRepository")
    private val authRepository: AuthRepository,
) {
    operator fun invoke() {
        authRepository.logout()
    }
}