package com.hapataka.questwalk.core.remote.datasource

import com.google.firebase.firestore.CollectionReference
import com.hapataka.questwalk.core.dataapi.datasource.HistoryRemoteDataSource
import com.hapataka.questwalk.core.dataapi.model.HistoryDto
import com.hapataka.questwalk.core.remote.model.AchievementResponse
import com.hapataka.questwalk.core.remote.model.QuestResultResponse
import com.hapataka.questwalk.core.remote.util.generateUid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseHistoryDataSource @Inject constructor(
    private val historyCollection: CollectionReference,
) : HistoryRemoteDataSource {

    override suspend fun getUserHistories(userId: String): Result<List<HistoryDto>> = runCatching {
        withContext(Dispatchers.IO) {
            val questResultsDeferred = async {
                historyCollection
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("recordType", QuestResultResponse.RECORD_TYPE_QUEST)
                    .get().await()
            }
            val achievementsDeferred = async {
                historyCollection
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("recordType", AchievementResponse.RECORD_TYPE_ACHIEVEMENT)
                    .get().await()
            }

            val questResults = questResultsDeferred.await()
                .documents
                .mapNotNull { doc ->
                    doc.toObject(QuestResultResponse::class.java)?.toDto(doc.id)
                }

            val achievements = achievementsDeferred.await()
                .documents
                .mapNotNull { doc ->
                    doc.toObject(AchievementResponse::class.java)?.toDto(doc.id)
                }

            questResults + achievements
        }
    }

    override suspend fun postHistory(historyDto: HistoryDto): Result<String> = runCatching {
        withContext(Dispatchers.IO) {
            val resultId = (historyDto.userId + System.currentTimeMillis()).generateUid()

            val response = when (historyDto) {
                is HistoryDto.QuestResultDto -> historyDto.toResponse()
                is HistoryDto.AchievementDto -> historyDto.toResponse()
            }

            historyCollection.document(resultId).set(response).await()
            resultId
        }
    }

    override suspend fun getQuestResult(resultId: String): Result<HistoryDto.QuestResultDto> = runCatching {
        withContext(Dispatchers.IO) {
            val doc = historyCollection.document(resultId).get().await()
            doc.toObject(QuestResultResponse::class.java)?.toDto(doc.id)
                ?: throw NoSuchElementException("Quest result not found: $resultId")
        }
    }

    override suspend fun deleteHistoriesByUserId(userId: String): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            val documents = historyCollection
                .whereEqualTo("userId", userId)
                .get().await()
                .documents

            documents.forEach { doc ->
                historyCollection.document(doc.id).delete().await()
            }
        }
    }

    // Response -> DTO 변환
    private fun QuestResultResponse.toDto(documentId: String): HistoryDto.QuestResultDto =
        HistoryDto.QuestResultDto(
            resultId = documentId,
            userId = userId,
            registerAt = registerAt,
            registerAtUtc = registerAtUtc,
            questKeyword = questKeyword,
            duration = duration,
            distance = distance,
            step = step,
            isSuccess = isSuccess,
            route = route,
            successLocation = successLocation,
            imageUrl = questImg,
        )

    private fun AchievementResponse.toDto(documentId: String): HistoryDto.AchievementDto =
        HistoryDto.AchievementDto(
            resultId = documentId,
            userId = userId,
            registerAt = registerAt,
            achievementId = achievementId,
        )

    // DTO -> Response 변환
    private fun HistoryDto.QuestResultDto.toResponse(): QuestResultResponse =
        QuestResultResponse(
            userId = userId,
            registerAt = registerAt,
            registerAtUtc = registerAtUtc,
            questKeyword = questKeyword,
            duration = duration,
            distance = distance,
            step = step,
            isSuccess = isSuccess,
            route = route,
            successLocation = successLocation,
            questImg = imageUrl,
            recordType = QuestResultResponse.RECORD_TYPE_QUEST,
        )

    private fun HistoryDto.AchievementDto.toResponse(): AchievementResponse =
        AchievementResponse(
            userId = userId,
            registerAt = registerAt,
            achievementId = achievementId,
            recordType = AchievementResponse.RECORD_TYPE_ACHIEVEMENT,
        )
}