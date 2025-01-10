package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Named

class LoginUseCase @Inject constructor(
    @Named("DefaultAuthRepository")
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        return when {
            email.isBlank() -> return Result.failure(IllegalArgumentException("이메일을 입력해 주세요"))
            password.isBlank() -> return Result.failure(IllegalArgumentException("비밀번호를 입력해 주세요"))
            else -> return authRepository.loginWithEmail(email, password)
        }
    }
}