package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.UserRepositoryNew
import javax.inject.Inject
import javax.inject.Named

class UpdateUserInfoUseCase @Inject constructor(
    @Named("DefaultUserRepository")
    private val userRepository: UserRepositoryNew,
) {
    suspend operator fun invoke(
        time: Long,
        distance: Float,
        step: Long,
        keyword: String,
        achievementId: Int? = null
    ): Result<Unit> {
        return kotlin.runCatching {
            userRepository.updateUserInfo(
                time = time,
                distance = distance,
                step = step,
                keyword = keyword,
                achievementId = achievementId
            )
        }
    }
}