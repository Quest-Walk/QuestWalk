package com.hapataka.questwalk.core.remote.model

import com.hapataka.questwalk.core.model.User

data class UserDto(
    val userId: String = "",
    var userName: String = "",
    var characterId: Int = 1,
    var totalTime: Long = 0,
    var totalDistance: Float = 0f,
    var totalStep: Long = 0,
)

fun UserDto.toModel(): User {
    return User(
        userId = this.userId,
        userName = this.userName,
        characterType = characterId,
        totalTime = this.totalTime,
        totalDistance = this.totalDistance,
        totalStep = this.totalStep,
    )
}