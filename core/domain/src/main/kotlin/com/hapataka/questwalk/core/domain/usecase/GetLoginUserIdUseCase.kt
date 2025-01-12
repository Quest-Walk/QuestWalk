package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Named

class GetLoginUserIdUseCase @Inject constructor(
    @Named("DefaultAuthRepository")
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Result<String> {
        return kotlin.runCatching { authRepository.getUserId() }
    }
}