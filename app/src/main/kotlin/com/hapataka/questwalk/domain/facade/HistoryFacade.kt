package com.hapataka.questwalk.domain.facade

import com.hapataka.questwalk.core.model.History
import com.hapataka.questwalk.core.model.History.Achievement
import com.hapataka.questwalk.core.model.History.QuestResult
import com.hapataka.questwalk.domain.usecase.CacheCurrentUserHistoriesUseCase
import com.hapataka.questwalk.domain.usecase.GetCurrentUserHistoriesUseCase
import javax.inject.Inject

const val RESULT_SUCCESS_COUNT = 0
const val ACHIEVEMENT_COUNT = 1

class HistoryFacade @Inject constructor(
    private val cacheCurrentUserHistoriesUseCase: CacheCurrentUserHistoriesUseCase,
    private val getCurrentUserHistoriesUseCase: GetCurrentUserHistoriesUseCase,
) {
    suspend fun cacheCurrentUserHistories() {
        cacheCurrentUserHistoriesUseCase()
    }

    fun countCurrentUserHistories(): Map<Int, Int> {
        val result = mutableMapOf<Int, Int>()
        val histories = getCurrentUserHistoriesUseCase()

        result[RESULT_SUCCESS_COUNT] =
            histories?.filterIsInstance<QuestResult>()?.filter { it.isSuccess }?.size ?: 0
        result[ACHIEVEMENT_COUNT] = histories?.filterIsInstance<Achievement>()?.size ?: 0
        return result
    }

    fun getCurrentUserHistories(): List<History>? {
        return getCurrentUserHistoriesUseCase()
    }
}