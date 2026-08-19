package com.hapataka.questwalk.core.dataapi.model

sealed class HistoryDto(
    open val resultId: String,
    open val userId: String,
    open val registerAt: String,
) {
    data class QuestResultDto(
        override val resultId: String = "",
        override val userId: String = "",
        override val registerAt: String = "",
        val registerAtUtc: String = "",
        val questKeyword: String = "",
        val duration: Long = 0L,
        val distance: Float = 0f,
        val step: Long = 0L,
        val isSuccess: Boolean = false,
        val route: String = "",
        val successLocation: String? = null,
        val imageUrl: String? = null,
    ) : HistoryDto(resultId, userId, registerAt)

    data class AchievementDto(
        override val resultId: String = "",
        override val userId: String = "",
        override val registerAt: String = "",
        val achievementId: Int = -1,
    ) : HistoryDto(resultId, userId, registerAt)
}
