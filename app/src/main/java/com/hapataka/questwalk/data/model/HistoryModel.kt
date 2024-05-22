package com.hapataka.questwalk.data.model

sealed class HistoryModel {
    data class ResultRecord(
        val questKeyword: String = "",
        val duration: Long = 0L,
        val distance: Float = 0f,
        val step: Long = 0L,
        val isSuccess: Boolean = false,
        val route: List<Pair<Float, Float>>?,
        val successLocation: Pair<Float, Float>? = null,
        val questImg: String? = null
    ) : HistoryModel()

    data class AchievementRecord(
        val achievementId: Int,
    ) : HistoryModel()
}
