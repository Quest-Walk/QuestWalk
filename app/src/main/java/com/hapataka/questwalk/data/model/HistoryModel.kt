package com.hapataka.questwalk.data.model

import java.time.LocalDateTime

sealed class HistoryModel(open val userId: String, open val registerAt: LocalDateTime) {
    data class ResultRecord(
        override val userId: String,
        override val registerAt: LocalDateTime,
        val questKeyword: String = "",
        val duration: Long = 0L,
        val distance: Float = 0f,
        val step: Long = 0L,
        val isSuccess: Boolean = false,
        val route: List<Pair<Float, Float>>?,
        val successLocation: Pair<Float, Float>? = null,
        val questImg: String? = null
    ) : HistoryModel(userId, registerAt)

    data class AchievementRecord(
        override val userId: String,
        override val registerAt: LocalDateTime,
        val achievementId: Int,
    ) : HistoryModel(userId, registerAt)
}
