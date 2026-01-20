package com.hapataka.questwalk.data.repository

import com.hapataka.questwalk.core.domain.repository.HistoryRepository
import com.hapataka.questwalk.core.model.History
import com.hapataka.questwalk.core.model.History.Achievement
import com.hapataka.questwalk.core.model.History.QuestResult
import com.hapataka.questwalk.core.model.Location
import com.hapataka.questwalk.core.remote.util.decryptECB
import com.hapataka.questwalk.domain.data.remote.AchievementsDataSource
import com.hapataka.questwalk.domain.data.remote.HistoryRDS
import com.hapataka.questwalk.util.UserInfo
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Named

class HistoryRepositoryImpl @Inject constructor(
    @Named("FirebaseHistoryRDS")
    private val firebaseHistoryRDS: HistoryRDS,
    @Named("FirebaseAchievementsDataSource")
    private val firebaseAchievementsDataSource: AchievementsDataSource,
) : HistoryRepository {
    override suspend fun getUserHistories(userId: String): Result<List<History>> {
        return kotlin.runCatching {

            val result = mutableListOf<History>()
            val achievementItems = firebaseAchievementsDataSource.getAchievements()


            firebaseHistoryRDS.getHistoriesById(userId).let { histories ->
                histories.resultRecords.forEach {
                    result += QuestResult(
                        id = "${it.userId}_${it.registerAt}",
                        userId = it.userId,
                        registerAt = LocalDateTime.parse(it.registerAt),
                        questKeyword = it.questKeyword,
                        duration = it.duration,
                        distance = it.distance,
                        step = it.step,
                        isSuccess = it.isSuccess,
                        route = it.route.toRoute(),
                        successLocation = it.successLocation?.toSuccessLocation(),
                        imageUrl = it.questImg
                    )
                }
                histories.achievementRecords.forEach { achievementRecord ->
                    val item = achievementItems.find { it.id == achievementRecord.achievementId }
                        ?: return@forEach

                    result += Achievement(
                        id = "${achievementRecord.userId}_${achievementRecord.registerAt}",
                        userId = achievementRecord.userId,
                        registerAt = LocalDateTime.parse(achievementRecord.registerAt),
                        achievementId = achievementRecord.achievementId,
                        description = item.description,
                    )
                }
            }
            result
        }
    }

    override suspend fun getQuestResult(resultId: String): Result<QuestResult> {
        TODO("Not yet implemented")
    }

    override suspend fun postHistory(userId: String, history: History): Result<String> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteHistoriesById(userId: String): Result<Unit> {
        return firebaseHistoryRDS.deleteHistoriesById(userId)
    }

    private fun String.toRoute(): List<Location> {
        val pairs: List<Pair<Float, Float>> = Json.decodeFromString(this.decryptECB(UserInfo.encryptionKey))
        return pairs.map { Location(latitude = it.first, longitude = it.second) }
    }

    private fun String.toSuccessLocation(): Location {
        val pair: Pair<Float, Float> = Json.decodeFromString(this.decryptECB(UserInfo.encryptionKey))
        return Location(latitude = pair.first, longitude = pair.second)
    }
}
