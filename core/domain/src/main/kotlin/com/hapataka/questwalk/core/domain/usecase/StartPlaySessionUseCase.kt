package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.PlaySessionRepository
import com.hapataka.questwalk.core.domain.repository.QuestRepository
import javax.inject.Inject

class StartPlaySessionUseCase @Inject constructor(
    private val playSessionRepository: PlaySessionRepository,
    private val questRepository: QuestRepository,
) {
    operator fun invoke() {
        val quest = questRepository.currentQuest.value
            ?: throw IllegalStateException("No quest selected")
        playSessionRepository.startSession(quest.keyword, quest.level)
    }
}
