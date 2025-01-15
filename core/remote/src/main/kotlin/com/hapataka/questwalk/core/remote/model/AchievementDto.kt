package com.hapataka.questwalk.core.remote.model

data class AchievementDto(
    val resultId: String = "",
    val userId: String = "",
    val registerAt: String = "",
    val achievementId: Int = -1,
    val type: Int = 2,
)