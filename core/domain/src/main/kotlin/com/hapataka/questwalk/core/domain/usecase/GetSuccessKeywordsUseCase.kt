package com.hapataka.questwalk.core.domain.usecase

import com.hapataka.questwalk.core.domain.repository.HistoryRepository
import com.hapataka.questwalk.core.model.History
import javax.inject.Inject

class GetSuccessKeywordsUseCase @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val getLoginUserIdUseCase: GetLoginUserIdUseCase,
) {
    suspend operator fun invoke(): Result<Set<String>> {
        return runCatching {
            val userId = getLoginUserIdUseCase().getOrThrow()
            val histories = historyRepository.getUserHistories(userId).getOrElse { emptyList() }

            histories
                .filterIsInstance<History.QuestResult>()
                .filter { it.isSuccess }
                .map { it.questKeyword }
                .toSet()
        }
    }
}
