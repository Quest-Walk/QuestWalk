package com.hapataka.questwalk.domain.entity

data class UserEntity(
    val id: String = "",
    var nickName: String = "",
    var characterId: Int = -1,
    var totalTime: String = "",
    var totalDistance: Float = 0f,
    var totalStep: Long = 0,
    var histories: MutableList<HistoryEntity> = mutableListOf()
)
