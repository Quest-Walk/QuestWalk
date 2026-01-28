package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.QuestRepository
import com.hapataka.questwalk.core.model.Quest
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetCurrentQuestUseCase @Inject constructor(
    private val questRepository: QuestRepository,
) {
    operator fun invoke(): StateFlow<Quest?> {
        return questRepository.currentQuest
    }
}
