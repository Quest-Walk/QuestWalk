package com.hapataka.questwalk.domain.entity

import com.hapataka.questwalk.domain.entity.HistoryEntity.AchieveResultEntity
import com.hapataka.questwalk.domain.entity.HistoryEntity.ResultEntity

class UserEntity(
    val id: String = "",
    var nickName: String = "",
    var characterId: Int = -1,
    var totalTime: String = "",
    var totalDistance: Float = 0f,
    var totalStep: Long = 0,
    var histories: MutableList<HistoryEntity> = mutableListOf()
) {
    fun getOnlyResult(): List<HistoryEntity> {
        return histories.filterIsInstance<ResultEntity>()
    }

    fun getOnlyAchieve(): List<HistoryEntity> {
        return histories.filterIsInstance<AchieveResultEntity>()
    }

    fun updateHistory(newHistory: HistoryEntity) {
        histories += newHistory
    }

    fun updateNickName(newName: String) {
        if (newName.isNotEmpty()) {
            nickName = newName
        } else {
            throw IllegalArgumentException("닉네임이 입력되지 않았습니다.")
        }
    }

    fun updateRecords(time: Long, distance: Float, step: Long) {
        if (distance > 10) {
            totalTime = (totalTime.toLong() + time).toString()
            totalDistance += distance
            totalStep += step
        } else {
            throw IllegalArgumentException("기록이 기준 이하입니다.")
        }
    }
}
