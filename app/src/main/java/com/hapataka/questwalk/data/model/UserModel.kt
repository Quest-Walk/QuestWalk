package com.hapataka.questwalk.data.model

data class UserModel(
    val userId: String,
    var nickName: String = "",
    var characterId: Int = 1,
    var totalTime: Long = 0L,
    var totalDistance: Float = 0f,
    var totalStep: Long = 0L,
) {
    fun changeNickName(newNickName: String) {
        this.nickName = newNickName
    }

    fun changeCharacter(newCharacterId: Int) {
        this.characterId = newCharacterId
    }

    fun updateTotalInfo(time: Long, distance: Float, step: Long) {
        this.totalTime += time
        this.totalDistance += distance
        this.totalStep += step
    }
}
