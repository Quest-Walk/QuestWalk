package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.AuthRepository
import com.hapataka.questwalk.core.domain.repository.UserRepositoryNew
import javax.inject.Inject
import javax.inject.Named

class JoinWithEmailUseCase @Inject constructor(
    @Named("DefaultAuthRepository")
    private val authRepository: AuthRepository,
    @Named("DefaultUserRepository")
    private val userRepository: UserRepositoryNew,
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        return runCatching {
            require(email.isNotBlank()) { "이메일을 입력해 주세요" }
            require(password.isNotBlank()) { "비밀번호를 입력해 주세요" }

            val uid = authRepository.joinWithEmail(email, password).getOrElse { e ->
                throw Exception(e.message?.toKoreanMessage() ?: "회원가입에 실패했습니다")
            }
            authRepository.setLastEmail(email)
            userRepository.insertUser(uid)
        }
    }

    private fun String.toKoreanMessage(): String {
        return when {
            contains("email address is already in use") -> "이미 사용 중인 이메일입니다"
            contains("email address is badly formatted") -> "이메일 형식이 올바르지 않습니다"
            contains("Password should be at least 6 characters") -> "비밀번호는 6자 이상이어야 합니다"
            contains("weak password") -> "비밀번호가 너무 약합니다"
            contains("network error") -> "네트워크 오류가 발생했습니다"
            contains("too many requests") -> "잠시 후 다시 시도해 주세요"
            else -> this
        }
    }
}