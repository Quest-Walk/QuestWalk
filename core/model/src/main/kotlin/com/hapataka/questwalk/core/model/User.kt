package com.hapataka.questwalk.core.model

data class User(
    val userId: String,
    val userName: String = "",
    val characterType: Int = 1,
    val totalTime: Long = 0,
    val totalDistance: Float = 0f,
    val totalStep: Long = 0,
    val successKeywords: List<String> = emptyList(),
    val achievementIds: List<Int> = emptyList(),
)