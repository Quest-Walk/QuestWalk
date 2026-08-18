package com.hapataka.questwalk.core.domain.repository

import com.hapataka.questwalk.core.model.CharacterType
import com.hapataka.questwalk.core.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepositoryNew {
    suspend fun checkUserLoggedIn(): Boolean
    suspend fun insertUser(userId: String)
    suspend fun clearUserInfo()
    fun getUserInfo(): Flow<User?>
    suspend fun fetchUserInfo(): Result<Unit>
    suspend fun postUserInfo(
        userId: String,
        userName: String,
        characterType: CharacterType,
    ): Result<Unit>

    /** 마지막으로 누적 집계에 반영된 기록의 UTC 시각. 아직 집계한 적이 없으면 빈 문자열. */
    suspend fun getLastAggregatedAt(userId: String): Result<String>

    /** 히스토리에서 다시 계산한 누적값을 절대값으로 반영한다. */
    suspend fun applyAggregate(
        userId: String,
        totalTime: Long,
        totalDistance: Float,
        totalStep: Long,
        successKeywords: List<String>,
        achievementIds: List<Int>,
        lastAggregatedAt: String,
    ): Result<Unit>
}
