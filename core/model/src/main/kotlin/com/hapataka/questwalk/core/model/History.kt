package com.hapataka.questwalk.core.model

import java.time.LocalDateTime

sealed class History(
    open val userId: String,
    open val registerAt: LocalDateTime,
) {
    data class QuestResult(
        override val userId: String,
        override val registerAt: LocalDateTime,
        val questKeyword: String,
        val duration: Long,
        val distance: Float,
        val step: Long,
        val isSuccess: Boolean,
        val route: List<Pair<Float, Float>>,
        val successLocation: Pair<Float, Float>?,
        val imageUrl: String? = null,
    ) : History(userId, registerAt)

    data class Achievement(
        override val userId: String,
        override val registerAt: LocalDateTime,
        val achievementId: Int,
        val description: String = "",
    ) : History(userId, registerAt)
}
