package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Named

class GetLastEmailUseCase @Inject constructor(
    @Named("DefaultAuthRepository")
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<String?> {
        return authRepository.getLastEmail()
    }
}
