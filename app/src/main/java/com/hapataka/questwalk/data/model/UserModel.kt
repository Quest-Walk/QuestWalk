package com.hapataka.questwalk.data.model

import com.hapataka.questwalk.domain.entity.HistoryEntity

data class UserModel(
    val id: String = "",
    var nickName: String = "",
    var characterId: Int = -1,
    var totalTime: String = "",
    var totalDistance: Float = 0f,
    var totalStep: Long = 0,
    var histories: MutableList<HistoryEntity> = mutableListOf()
) {
    fun changeNickName(newNickName: String) {
        this.nickName = newNickName
    }
}
