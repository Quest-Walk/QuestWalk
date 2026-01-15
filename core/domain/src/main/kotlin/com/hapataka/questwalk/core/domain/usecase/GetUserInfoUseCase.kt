package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.UserRepositoryNew
import com.hapataka.questwalk.core.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Named

class GetUserInfoUseCase @Inject constructor(
    @Named("DefaultUserRepository")
    private val userRepository: UserRepositoryNew,
) {
    suspend operator fun invoke(): Flow<User?> {
        return userRepository.getUserInfo()
    }
}