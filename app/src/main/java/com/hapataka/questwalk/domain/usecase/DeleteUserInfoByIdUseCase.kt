package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.domain.repository.HistoryRepository
import com.hapataka.questwalk.domain.repository.UserRepository
import javax.inject.Inject

class DeleteUserInfoByIdUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(userId: String): Result<Unit> {
        return kotlin.runCatching {
            val historiesDeleted = historyRepository.deleteHistoriesById(userId)

            if (historiesDeleted.isSuccess) {
                userRepository.deleteUserById(userId)
            }
        }
    }
}