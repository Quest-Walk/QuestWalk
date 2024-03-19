package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.domain.entity.HistoryEntity
import com.hapataka.questwalk.domain.entity.UserEntity

object AchievementListener {


    operator fun invoke(userInfo: UserEntity): List<Int> {
        val successCount =
            userInfo.histories.filterIsInstance<HistoryEntity.ResultEntity>().filter {
                it.isSuccess
            }.size
        val results = mutableListOf<Int>()

        if (userInfo.totalDistance > 42195f) {
            results += 2
        }

        if (successCount >= 1) {
            results += 0
        }

        if (successCount >= 3) {
            results += 1
        }
        return results
    }
}