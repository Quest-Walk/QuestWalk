package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.data.model.UserModel
import com.hapataka.questwalk.domain.repository.CacheRepository
import javax.inject.Inject

class UpdateUserNameUseCase @Inject constructor(
    private val cacheRepository: CacheRepository
) {
    suspend operator fun invoke(newName: String): UserModel? {
        val user = cacheRepository.getCurrentUser() ?: return null

        user.changeNickName(newName)
        return user
    }
}