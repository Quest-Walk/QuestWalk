package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.AuthRepository
import com.hapataka.questwalk.core.domain.repository.UserRepositoryNew
import javax.inject.Inject
import javax.inject.Named

class LoginUseCase @Inject constructor(
    @Named("DefaultAuthRepository")
    private val authRepository: AuthRepository,
    @Named("DefaultUserRepository")
    private val userRepository: UserRepositoryNew,
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        return kotlin.runCatching {
            when {
                email.isBlank() -> throw IllegalArgumentException("이메일을 입력해 주세요")
                password.isBlank() -> throw IllegalArgumentException("비밀번호를 입력해 주세요")
                else -> {
                    authRepository.loginWithEmail(email, password)
                        .onSuccess { uid -> saveUserId(uid) }
                }
            }
        }
    }

    suspend operator fun invoke(idToken: String): Result<Unit> {
        return kotlin.runCatching {
            authRepository.loginWithGoogle(idToken)
                .onSuccess { uid -> saveUserId(uid) }
        }
    }

    private suspend fun saveUserId(userId: String) {
        userRepository.insertUser(userId)
    }
}