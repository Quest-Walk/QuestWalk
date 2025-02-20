package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.UserRepositoryNew
import javax.inject.Inject
import javax.inject.Named

class FetchUserInfoUseCase @Inject constructor(
    @Named("DefaultUserRepository")
    private val userRepository: UserRepositoryNew,
) {
    suspend operator fun invoke(): Result<Unit> {
        val result = userRepository.fetchUserInfo()

        return result
    }
}