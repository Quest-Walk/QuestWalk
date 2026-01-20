package com.hapataka.questwalk.core.dataapi.model

data class UserDto(
    val userId: String = "",
    val userName: String = "",
    val characterId: Int = 1,
    val totalTime: Long = 0,
    val totalDistance: Float = 0f,
    val totalStep: Long = 0,
    val successKeywords: List<String> = emptyList(),
    val achievementIds: List<Int> = emptyList(),
)
