package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.PlaySessionRepository
import javax.inject.Inject

class ResetPlaySessionUseCase @Inject constructor(
    private val playSessionRepository: PlaySessionRepository,
) {
    operator fun invoke() {
        playSessionRepository.resetSession()
    }
}
