package com.hapataka.questwalk.data.repository

import com.hapataka.questwalk.data.model.HistoryModel
import com.hapataka.questwalk.data.model.HistoryModel.AchievementRecordModel
import com.hapataka.questwalk.data.model.HistoryModel.ResultRecordModel
import com.hapataka.questwalk.domain.data.remote.AchievementsDataSource
import com.hapataka.questwalk.domain.data.remote.HistoryRDS
import com.hapataka.questwalk.domain.repository.HistoryRepository
import com.hapataka.questwalk.util.extentions.decryptECB
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Named

class HistoryRepositoryImpl @Inject constructor(
    @Named("FirebaseHistoryRDS")
    private val firebaseHistoryRDS: HistoryRDS,
    @Named("FirebaseAchievementsDataSource")
    private val firebaseAchievementsDataSource: AchievementsDataSource
) : HistoryRepository {
    override suspend fun getUserHistory(userId: String): List<HistoryModel> {
        val result = mutableListOf<HistoryModel>()
        val achievementItems = firebaseAchievementsDataSource.getAchievements()


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
            histories.achievementRecords.forEach { achievementRecord ->
                val item = achievementItems.find { it.id == achievementRecord.achievementId } ?: return@forEach

                result += AchievementRecordModel(
                    userId = achievementRecord.userId,
                    registerAt = LocalDateTime.parse(achievementRecord.registerAt),
                    achievementId = achievementRecord.achievementId,
                    description = item.description,
                    title = item.title,
                    successCount = item.successCount,
                    iconUrl = item.iconUrl
                )
            }
        }
        return result
    }

    override suspend fun deleteHistoriesById(userId: String): Result<Unit> {
        return firebaseHistoryRDS.deleteHistoriesById(userId)
    }

    private fun String.toRoute(): MutableList<Pair<Float, Float>> {
        return Json.decodeFromString(this.decryptECB())
    }

    private fun String.toSuccessLocation(): Pair<Float, Float> {
        return Json.decodeFromString(this.decryptECB())
    }
}
