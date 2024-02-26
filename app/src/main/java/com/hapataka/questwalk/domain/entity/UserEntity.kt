package com.hapataka.questwalk.domain.entity

data class UserEntity(
    val id: String,
    var password: String,
    var nickName: String,
    var characterId: Int,
    var totalTime: String,
    var totalDistance: Float,
    var totalStep: Int,
    var history: List<History>
)
