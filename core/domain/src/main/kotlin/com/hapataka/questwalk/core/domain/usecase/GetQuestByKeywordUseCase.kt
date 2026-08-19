package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.QuestRepository
import com.hapataka.questwalk.core.model.Quest
import javax.inject.Inject

class GetQuestByKeywordUseCase @Inject constructor(
    private val questRepository: QuestRepository,
) {
    suspend operator fun invoke(keyword: String): Result<Quest> {
        return questRepository.getQuestByKeyword(keyword)
    }
}
