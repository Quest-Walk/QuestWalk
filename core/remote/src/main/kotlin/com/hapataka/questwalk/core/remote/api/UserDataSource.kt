package com.hapataka.questwalk.core.remote.api

import com.hapataka.questwalk.core.model.CharacterType
import com.hapataka.questwalk.core.model.User

interface UserDataSource {
    suspend fun getUserInfo(userId: String): Result<User>
    suspend fun postUserInfo(
        userId: String,
        userName: String,
        characterType: CharacterType,
    ): Result<Unit>

    /** 누적 집계의 기준선. 없으면 빈 문자열. */
    suspend fun getLastAggregatedAt(userId: String): String

    /**
     * 히스토리에서 다시 계산한 누적값을 절대값으로 덮어쓴다.
     * 읽고-더하는 방식이 아니라 몇 번 실행돼도 결과가 같다.
     */
    suspend fun setUserAggregate(
        userId: String,
        totalTime: Long,
        totalDistance: Float,
        totalStep: Long,
        successKeywords: List<String>,
        achievementIds: List<Int>,
        lastAggregatedAt: String,
    )
}
