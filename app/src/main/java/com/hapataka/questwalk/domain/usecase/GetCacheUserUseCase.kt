package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.data.model.UserModel
import com.hapataka.questwalk.domain.repository.UserRepository
import javax.inject.Inject

class GetCacheUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): UserModel? {
        return userRepository.getCachedUser()
    }
}