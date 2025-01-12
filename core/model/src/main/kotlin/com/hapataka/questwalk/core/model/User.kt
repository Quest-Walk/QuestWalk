package com.hapataka.questwalk.core.model

data class User(
    val id: String,
    var userName: String,
    var characterType: CharacterType,
    var totalTime: Long,
    var totalDistance: Float,
    var totalStep: Long,
    var historiesId: String,
)
