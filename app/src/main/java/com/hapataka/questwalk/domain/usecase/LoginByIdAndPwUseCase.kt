package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.domain.repository.AuthRepository
import com.hapataka.questwalk.domain.repository.UserRepository
import javax.inject.Inject

class LoginByIdAndPwUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(id: String, pw: String) {
        authRepository.loginByIdAndPw(id, pw)
    }
}