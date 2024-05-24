package com.hapataka.questwalk.data.model

data class UserModel(
    val userId: String,
    var nickName: String = "이름 없음",
    var characterId: Int = 1,
    var totalTime: Int = 0,
    var totalDistance: Float = 0f,
    var totalStep: Long = 0,
) {
    fun changeNickName(newNickName: String) {
        this.nickName = newNickName
    }

    fun changeCharacter(newCharacterId: Int) {
        this.characterId = newCharacterId
    }

    fun updateTotalInfo(time: Int, distance: Float, step: Long) {
        this.totalTime += time
        this.totalDistance += distance
        this.totalStep += step
    }
}
