package com.hapataka.questwalk.data.dto

data class AchievementDTO(
    val id: String,
    val registerAt: String,
    val achievementId: Int,
    val type: Int = ACHIEVEMENT
)
