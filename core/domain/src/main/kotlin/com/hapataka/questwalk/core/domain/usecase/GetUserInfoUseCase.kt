package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.UserRepositoryNew
import javax.inject.Inject
import javax.inject.Named

class GetUserInfoUseCase @Inject constructor(
    @Named("DefaultUserRepository")
    private val userRepository: UserRepositoryNew,
) {
    suspend operator fun invoke(userId: String): Result<Unit> {
        return kotlin.runCatching {
            val userInfo = userRepository.getUserInfo(userId).getOrThrow()
            // TODO: 로컬에 사용자 정보 캐싱
        }
    }
}