package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.AuthRepository
import com.hapataka.questwalk.core.domain.repository.HistoryRepository
import com.hapataka.questwalk.core.domain.repository.UserRepositoryNew
import javax.inject.Inject
import javax.inject.Named

class DeleteAccountUseCase @Inject constructor(
    @Named("DefaultAuthRepository")
    private val authRepository: AuthRepository,
    @Named("DefaultUserRepository")
    private val userRepository: UserRepositoryNew,
    private val historyRepository: HistoryRepository,
) {
    suspend operator fun invoke(): Result<Unit> {
        return runCatching {
            val userId = authRepository.getUserId()

            // 1. 사용자 히스토리 삭제
            historyRepository.deleteHistoriesById(userId)

            // 2. 로컬 사용자 정보 삭제
            userRepository.clearUserInfo()

            // 3. Firebase 계정 삭제
            authRepository.deleteAccount().getOrThrow()
        }
    }
}
