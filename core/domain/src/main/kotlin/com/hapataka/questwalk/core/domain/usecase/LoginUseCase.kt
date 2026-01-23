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
    suspend fun withEmail(email: String, password: String): Result<Unit> {
        return runCatching {
            require(email.isNotBlank()) { "이메일을 입력해 주세요" }
            require(password.isNotBlank()) { "비밀번호를 입력해 주세요" }

            val uid = authRepository.loginWithEmail(email, password).getOrThrow()
            saveUserId(uid)
        }
    }

    suspend fun withGoogle(idToken: String): Result<Unit> {
        return runCatching {
            val uid = authRepository.loginWithGoogle(idToken).getOrThrow()
            saveUserId(uid)
        }
    }

    private suspend fun saveUserId(userId: String) {
        userRepository.insertUser(userId)
    }
}