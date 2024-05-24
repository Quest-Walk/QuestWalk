package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.data.model.UserModel
import com.hapataka.questwalk.domain.repository.AuthRepository
import com.hapataka.questwalk.domain.repository.UserRepository
import javax.inject.Inject

class RegisterByIdAndPwUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(id: String, pw: String) {
        try {
            authRepository.registerByIdAndPw(id, pw)
            userRepository.cacheUser(UserModel(id))
        } catch (e: Exception) {
            throw e
        }
    }
}