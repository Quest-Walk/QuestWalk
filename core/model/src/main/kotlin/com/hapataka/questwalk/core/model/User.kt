package com.hapataka.questwalk.core.model

data class User(
    val userId: String,
    val userName: String,
    val characterType: Int,
    val totalTime: Long,
    val totalDistance: Float,
    val totalStep: Long,
    val successCount: Int,
    val achievementCount: Int,
)