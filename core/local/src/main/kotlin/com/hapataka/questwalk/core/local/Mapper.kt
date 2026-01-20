package com.hapataka.questwalk.core.local

import com.hapataka.questwalk.core.model.CharacterType
import com.hapataka.questwalk.core.model.User

fun UserEntity.toModel() = User(
    userId = this.userId,
    userName = this.userName,
    characterType = CharacterType.entries.find { it.id == this.characterType } ?: CharacterType.BEAR,
    totalTime = this.totalTime,
    totalDistance = this.totalDistance,
    totalStep = this.totalStep,
    successKeywords = this.successKeywords,
    achievementIds = this.achievementIds
)

fun User.toEntity() = UserEntity(
    userId = this.userId,
    userName = this.userName,
    characterType = this.characterType.id,
    totalTime = this.totalTime,
    totalDistance = this.totalDistance,
    totalStep = this.totalStep,
    successKeywords = this.successKeywords,
    achievementIds = this.achievementIds
)