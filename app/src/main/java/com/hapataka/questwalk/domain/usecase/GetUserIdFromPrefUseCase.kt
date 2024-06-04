package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserIdFromPrefUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Flow<String?> {
        return userRepository.getUserIdFromPref()
    }

}