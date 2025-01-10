package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.domain.repository.PrevAuthRepository
import javax.inject.Inject

class LoginByIdAndPwUseCase @Inject constructor(
    private val authRepository: PrevAuthRepository,
) {
    suspend operator fun invoke(id: String, pw: String): Result<Boolean> {
        return authRepository.loginByIdAndPw(id, pw)
    }
}