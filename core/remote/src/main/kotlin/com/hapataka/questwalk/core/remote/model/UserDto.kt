package com.hapataka.questwalk.core.remote.model

import com.hapataka.questwalk.core.model.User

data class UserDto(
    val id: String = "",
    var nickName: String = "",
    var characterId: Int = 1,
    var totalTime: Long = 0,
    var totalDistance: Float = 0f,
    var totalStep: Long = 0,
    var historiesId: String = "",
)

fun UserDto.toModel(): User {
    return User(
        id = this.id,
        nickName = this.nickName,
        characterId = this.characterId,
        totalTime = this.totalTime,
        totalDistance = this.totalDistance,
        totalStep = this.totalStep,
        historiesId = this.historiesId
    )
}