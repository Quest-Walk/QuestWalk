package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.PlaySessionRepository
import javax.inject.Inject

class StartPlaySessionUseCase @Inject constructor(
    private val playSessionRepository: PlaySessionRepository,
) {
    operator fun invoke(keyword: String, level: Int) {
        playSessionRepository.startSession(keyword, level)
    }
}
