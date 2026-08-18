package com.hapataka.questwalk.core.domain.repository

import com.hapataka.questwalk.core.model.History
import com.hapataka.questwalk.core.model.UserActivitySummary

interface HistoryRepository {
    /** 누적 집계용 요약. 경로를 복호화하지 않아 전체 조회보다 가볍다. */
    suspend fun getUserActivitySummary(userId: String): Result<UserActivitySummary>

    suspend fun getUserHistories(userId: String): Result<List<History>>
    suspend fun postHistory(userId: String, history: History): Result<String>
    suspend fun deleteHistoriesById(userId: String): Result<Unit>
    suspend fun getQuestResult(resultId: String): Result<History.QuestResult>
}