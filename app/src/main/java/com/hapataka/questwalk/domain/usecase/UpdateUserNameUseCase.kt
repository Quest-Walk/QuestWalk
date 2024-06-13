package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.data.model.UserModel
import com.hapataka.questwalk.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserNameUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(newName: String): UserModel? {
        val user = userRepository.getCachedUser() ?: return null

        user.changeNickName(newName)
        return user
    }
}