package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.common.model.CharacterType
import com.hapataka.questwalk.core.domain.repository.AuthRepository
import com.hapataka.questwalk.core.domain.repository.UserRepositoryNew
import javax.inject.Inject
import javax.inject.Named

class PostUserInfoUserCase @Inject constructor(
    @Named("DefaultUserRepository")
    private val userRepository: UserRepositoryNew,
    @Named("DefaultAuthRepository")
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(userName: String, characterType: CharacterType): Result<Unit> {
        return kotlin.runCatching {
            val userId = authRepository.getUserId()

            userRepository.postUserInfo(userId, userName, characterType)
        }
    }
}