package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterByIdAndPwUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(id: String, pw: String): Result<Boolean> {
        return authRepository.registerByIdAndPw(id, pw)
    }
}