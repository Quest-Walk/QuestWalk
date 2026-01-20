package com.hapataka.questwalk.core.model

data class User(
    val userId: String,
    val userName: String = "",
    val characterType: CharacterType = CharacterType.BEAR,
    val totalTime: Long = 0,
    val totalDistance: Float = 0f,
    val totalStep: Long = 0,
    val successKeywords: List<String> = emptyList(),
    val achievementIds: List<Int> = emptyList(),
)
