package com.hapataka.questwalk.core.model

data class User(
    val id: String,
    var nickName: String,
    var characterId: Int,
    var totalTime: Long,
    var totalDistance: Float,
    var totalStep: Long,
    var historiesId: String,
)
