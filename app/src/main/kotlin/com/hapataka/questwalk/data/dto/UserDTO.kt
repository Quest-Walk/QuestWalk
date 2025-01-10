package com.hapataka.questwalk.data.dto

data class UserDTO(
    val id: String = "",
    var nickName: String = "",
    var characterId: Int = -1,
    var totalTime: Long = 0L,
    var totalDistance: Float = 0f,
    var totalStep: Long = 0L,
)
