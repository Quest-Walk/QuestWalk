package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.PlaySessionRepository
import com.hapataka.questwalk.core.model.PlaySession
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetPlaySessionUseCase @Inject constructor(
    private val playSessionRepository: PlaySessionRepository,
) {
    operator fun invoke(): StateFlow<PlaySession> {
        return playSessionRepository.sessionState
    }
}
