package com.bongpal.domain

import javax.inject.Inject

class LoginUseCase @Inject constructor() {
    operator fun invoke(email: String, password: String): Result<Boolean> {
        return when {
            email.isBlank() -> return Result.failure(IllegalArgumentException("이메일을 입력해 주세요"))
            password.isBlank() -> return Result.failure(IllegalArgumentException("비밀번호를 입력해 주세요"))
            else -> return Result.success(true)
        }
    }
}