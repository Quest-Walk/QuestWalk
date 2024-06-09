package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.domain.repository.UserRepository
import javax.inject.Inject

class CheckCurrentUserNameUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Boolean {
        val userName = userRepository.getCachedUser()?.nickName ?: ""

        return userName.isNotEmpty()
    }
}