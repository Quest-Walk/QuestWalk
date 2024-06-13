package com.hapataka.questwalk.data.repository

import android.util.Log
import com.hapataka.questwalk.data.model.HistoryModel
import com.hapataka.questwalk.data.model.HistoryModel.AchievementRecordModel
import com.hapataka.questwalk.data.model.HistoryModel.ResultRecordModel
import com.hapataka.questwalk.domain.data.remote.HistoryRDS
import com.hapataka.questwalk.domain.repository.HistoryRepository
import com.hapataka.questwalk.ui.login.TAG
import com.hapataka.questwalk.util.extentions.decryptECB
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Named

class HistoryRepositoryImpl @Inject constructor(
    @Named("FirebaseHistoryRDS")
    private val firebaseHistoryRDS: HistoryRDS,
) : HistoryRepository {
    override suspend fun getUserHistory(userId: String): List<HistoryModel> {
        val result = mutableListOf<HistoryModel>()


        firebaseHistoryRDS.getHistoriesById(userId).let { histories ->
            histories.resultRecords.forEach {
                result += ResultRecordModel(
                    userId = it.userId,
                    registerAt = LocalDateTime.parse(it.registerAt),
                    questKeyword = it.questKeyword,
                    duration = it.duration,
                    distance = it.distance,
                    step = it.step,
                    isSuccess = it.isSuccess,
                    route = it.route.toRoute(),
                    successLocation = it.successLocation?.toSuccessLocation(),
                    questImg = it.questImg
                )
            }
            histories.achievementRecords.forEach {
                result += AchievementRecordModel(
                    userId = it.userId,
                    registerAt = LocalDateTime.parse(it.registerAt),
                    achievementId = it.achievementId
                )
            }
        }
        return result
    }

    private fun String.toRoute(): MutableList<Pair<Float, Float>> {
        return Json.decodeFromString(this.decryptECB())
    }

    private fun String.toSuccessLocation(): Pair<Float, Float> {
        return Json.decodeFromString(this.decryptECB())

    }
}
