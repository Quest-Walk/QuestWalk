package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.QuestRepository
import com.hapataka.questwalk.core.model.Quest
import javax.inject.Inject

class SelectRandomQuestUseCase @Inject constructor(
    private val getAvailableQuestsUseCase: GetAvailableQuestsUseCase,
    private val questRepository: QuestRepository,
) {
    suspend operator fun invoke(): Result<Quest> = runCatching {
        val availableQuests = getAvailableQuestsUseCase().getOrThrow()
        if (availableQuests.isEmpty()) {
            throw NoSuchElementException("No available quests")
        }
        val selected = availableQuests.random()
        questRepository.setCurrentQuest(selected)
        selected
    }
}
