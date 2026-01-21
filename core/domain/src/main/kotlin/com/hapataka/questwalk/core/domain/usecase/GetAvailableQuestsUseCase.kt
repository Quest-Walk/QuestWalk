package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.HistoryRepository
import com.hapataka.questwalk.core.domain.repository.QuestRepository
import com.hapataka.questwalk.core.model.History
import com.hapataka.questwalk.core.model.Quest
import javax.inject.Inject

class GetAvailableQuestsUseCase @Inject constructor(
    private val questRepository: QuestRepository,
    private val historyRepository: HistoryRepository,
    private val getLoginUserIdUseCase: GetLoginUserIdUseCase,
) {
    suspend operator fun invoke(): Result<List<Quest>> {
        return runCatching {
            val userId = getLoginUserIdUseCase().getOrThrow()
            val allQuests = questRepository.getAllQuests().getOrThrow()
            val histories = historyRepository.getUserHistories(userId).getOrElse { emptyList() }

            val successKeywords = histories
                .filterIsInstance<History.QuestResult>()
                .filter { it.isSuccess }
                .map { it.questKeyword }
                .toSet()

            allQuests.filterNot { it.keyword in successKeywords }
        }
    }
}
