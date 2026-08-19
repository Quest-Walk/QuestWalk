package com.hapataka.questwalk.core.model

import java.time.LocalDateTime

sealed class History(
    open val id: String,
    open val userId: String,
    open val registerAt: LocalDateTime,
) {
    data class QuestResult(
        override val id: String,
        override val userId: String,
        override val registerAt: LocalDateTime,
        val questKeyword: String,
        val duration: Long,
        val distance: Float,
        val step: Long,
        val isSuccess: Boolean,
        val route: List<Location>,
        val successLocation: Location?,
        val imageUrl: String? = null,
        val registerAtUtc: String = "",
    ) : History(id, userId, registerAt)

    data class Achievement(
        override val id: String,
        override val userId: String,
        override val registerAt: LocalDateTime,
        val achievementId: Int,
        val description: String = "",
    ) : History(id, userId, registerAt)
}
