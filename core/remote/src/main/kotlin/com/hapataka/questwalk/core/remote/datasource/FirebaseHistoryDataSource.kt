package com.hapataka.questwalk.core.remote.datasource

import com.google.firebase.firestore.CollectionReference
import com.hapataka.questwalk.core.model.History
import com.hapataka.questwalk.core.remote.api.HistoryDataSource
import com.hapataka.questwalk.core.remote.model.AchievementDto
import com.hapataka.questwalk.core.remote.model.QuestResultDto
import com.hapataka.questwalk.core.remote.util.encryptECB
import javax.inject.Inject

class FirebaseHistoryDataSource @Inject constructor(
    private val historyCollection: CollectionReference,
) : HistoryDataSource {
    private val tempKey = "Q2CR35WC121QCB4T"

    override suspend fun postHistory(userId: String, history: History): Result<Unit> {
        return kotlin.runCatching {
            when (history) {
                is History.QuestResult -> postQuestResult(userId, history)
                is History.Achievement -> postAchievement(userId, history)
            }
        }
    }

    private fun postQuestResult(userId: String, history: History.QuestResult) {
        historyCollection.add(
            QuestResultDto(
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
    }

    private fun postAchievement(userId: String, history: History.Achievement) {
        historyCollection.add(
            AchievementDto(
                userId = userId,
                registerAt = history.registerAt.toString(),
                achievementId = history.achievementId,
            )
        )
    }
}