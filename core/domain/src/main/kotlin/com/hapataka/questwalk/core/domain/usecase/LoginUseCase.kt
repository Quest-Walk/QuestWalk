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

            val uid = authRepository.loginWithEmail(email, password).getOrElse { e ->
                throw Exception(e.message?.toKoreanMessage() ?: "로그인에 실패했습니다")
            }
            authRepository.setLastEmail(email)
            saveUserId(uid)
        }
    }

    suspend fun withGoogle(idToken: String): Result<Unit> {
        return runCatching {
            val uid = authRepository.loginWithGoogle(idToken).getOrElse { e ->
                throw Exception(e.message?.toKoreanMessage() ?: "로그인에 실패했습니다")
            }
            saveUserId(uid)
        }
    }

    private suspend fun saveUserId(userId: String) {
        userRepository.insertUser(userId)
    }

    private fun String.toKoreanMessage(): String {
        return when {
            contains("no user record") -> "등록되지 않은 이메일입니다"
            contains("password is invalid") -> "비밀번호가 올바르지 않습니다"
            contains("email address is badly formatted") -> "이메일 형식이 올바르지 않습니다"
            contains("user has been disabled") -> "비활성화된 계정입니다"
            contains("network error") -> "네트워크 오류가 발생했습니다"
            contains("too many requests") -> "잠시 후 다시 시도해 주세요"
            contains("account exists with different credential") -> "다른 방식으로 가입된 계정입니다"
            else -> this
        }
    }
}