package com.hapataka.questwalk.core.remote.model

data class AchievementDto(
    val userId: String,
    val registerAt: String,
    val achievementId: Int,
    val type: Int = 2,
)