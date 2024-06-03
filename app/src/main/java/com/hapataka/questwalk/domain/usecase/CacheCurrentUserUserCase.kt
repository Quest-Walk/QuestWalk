package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.data.model.UserModel
import com.hapataka.questwalk.domain.repository.AuthRepository
import com.hapataka.questwalk.domain.repository.UserRepository
import javax.inject.Inject


class CacheCurrentUserUserCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        val currentUserId = authRepository.getCurrentUserId() ?: return
        val userModel = UserModel(currentUserId)

        userRepository.getUserById(currentUserId)?.let {
            userModel.updateTotalInfo(it.totalTime, it.totalDistance, it.totalStep)
            userModel.changeCharacter(it.characterId)
            userModel.changeNickName(it.nickName)
        }
        userRepository.cacheUser(userModel)
    }
}