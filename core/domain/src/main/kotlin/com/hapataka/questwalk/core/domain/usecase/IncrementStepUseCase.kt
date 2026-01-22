package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.PlaySessionRepository
import javax.inject.Inject

class IncrementStepUseCase @Inject constructor(
    private val playSessionRepository: PlaySessionRepository,
) {
    operator fun invoke() {
        playSessionRepository.incrementStep()
    }
}
