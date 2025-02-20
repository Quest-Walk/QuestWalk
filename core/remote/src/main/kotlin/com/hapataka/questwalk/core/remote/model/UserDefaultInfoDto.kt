package com.hapataka.questwalk.core.remote.model

import com.hapataka.questwalk.core.model.User

data class UserDefaultInfoDto(
    val userId: String = "",
    var userName: String = "",
    var characterId: Int = 1,
    var totalTime: Long = 0,
    var totalDistance: Float = 0f,
    var totalStep: Long = 0,
)

fun User.getDefaultInfo() = hashMapOf(
    "userId" to this.userId,
    "userName" to this.userName,
    "characterId" to this.characterType,
    "totalTime" to this.totalTime,
    "totalDistance" to this.totalDistance,
    "totalStep" to this.totalStep,
)

fun UserDefaultInfoDto.toModel(
    successKeywords: List<String> = emptyList(),
    achievementIds: List<Int> = emptyList(),
) = User(
    userId = this.userId,
    userName = this.userName,
    characterType = this.characterId,
    totalTime = this.totalTime,
    totalDistance = this.totalDistance,
    totalStep = this.totalStep,
    successKeywords = successKeywords,
    achievementIds = achievementIds
)