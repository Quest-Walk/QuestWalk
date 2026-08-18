package com.hapataka.questwalk.core.model

/**
 * 누적 집계에 필요한 값만 담은 요약.
 * 경로처럼 무거운 필드는 복호화하지 않는다.
 */
data class UserActivitySummary(
    val successQuests: List<SuccessQuest> = emptyList(),
    val achievementIds: List<Int> = emptyList(),
) {
    data class SuccessQuest(
        val questKeyword: String,
        val duration: Long,
        val distance: Float,
        val step: Long,
        val registerAtUtc: String,
    )
}
