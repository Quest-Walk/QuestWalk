package com.hapataka.questwalk.core.remote.datasource

import com.google.firebase.firestore.CollectionReference
import com.hapataka.questwalk.core.model.History
import com.hapataka.questwalk.core.remote.api.HistoryDataSource
import com.hapataka.questwalk.core.remote.mapper.toModel
import com.hapataka.questwalk.core.remote.model.AchievementDto
import com.hapataka.questwalk.core.remote.model.QuestResultDto
import com.hapataka.questwalk.core.remote.util.encryptECB
import com.hapataka.questwalk.core.remote.util.generateUid
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseHistoryDataSource @Inject constructor(
    private val historyCollection: CollectionReference,
) : HistoryDataSource {
    private val tempKey = "Q2CR35WC121QCB4T"

    override suspend fun getQuestResult(resultId: String): Result<History.QuestResult> {
        return kotlin.runCatching {
            val result = historyCollection.document(resultId).get().await()

            result.toObject(QuestResultDto::class.java)?.toModel(tempKey)
                ?: throw NoSuchElementException("입력한 아이디의 데이터가 존재하지 않습니다.")
        }
    }

    override suspend fun postHistory(userId: String, history: History): Result<String> {
        return when (history) {
            is History.QuestResult -> postQuestResult(userId, history)
            is History.Achievement -> postAchievement(userId, history)
        }
    }

    private fun postQuestResult(
        userId: String,
        history: History.QuestResult
    ): Result<String> {
        return kotlin.runCatching {
            val resultId = (userId + System.currentTimeMillis()).generateUid()

            historyCollection.document(resultId).set(
                QuestResultDto(
                    resultId = resultId,
                    userId = userId,
                    registerAt = history.registerAt.toString(),
                    questKeyword = history.questKeyword,
                    duration = history.duration,
                    distance = history.distance,
                    step = history.step,
                    isSuccess = history.isSuccess,
                    route = history.route.encryptECB(tempKey),
                    successLocation = history.successLocation?.encryptECB(tempKey),
                    imageUrl = history.imageUrl
                )
            )

            resultId
        }
    }

    private fun postAchievement(userId: String, history: History.Achievement): Result<String> {
        return kotlin.runCatching {
            val resultId = (userId + System.currentTimeMillis()).generateUid()

            historyCollection.document(resultId).set(
                AchievementDto(
                    resultId = resultId,
                    userId = userId,
                    registerAt = history.registerAt.toString(),
                    achievementId = history.achievementId,
                )
            )

            resultId
        }
    }
}