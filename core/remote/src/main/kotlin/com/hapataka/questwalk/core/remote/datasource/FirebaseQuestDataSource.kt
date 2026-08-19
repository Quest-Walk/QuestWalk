package com.hapataka.questwalk.core.remote.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.hapataka.questwalk.core.dataapi.datasource.QuestRemoteDataSource
import com.hapataka.questwalk.core.dataapi.model.QuestDto
import com.hapataka.questwalk.core.dataapi.model.SuccessItemDto
import com.hapataka.questwalk.core.remote.model.QuestResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseQuestDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) : QuestRemoteDataSource {

    private val questCollection by lazy { firestore.collection("quest") }

    override suspend fun getAllQuests(): Result<List<QuestDto>> = runCatching {
        withContext(Dispatchers.IO) {
            val results = mutableListOf<QuestResponse>()
            val levelList = intArrayOf(1, 2, 3)

            val deferredResults = levelList.map { level ->
                async {
                    questCollection.whereEqualTo("level", level).get().await()
                }
            }

            deferredResults.awaitAll().forEach { snapshot ->
                results += snapshot.toObjects(QuestResponse::class.java)
            }

            results.map { it.toDto() }
        }
    }

    override suspend fun getQuestByKeyword(keyword: String): Result<QuestDto?> = runCatching {
        withContext(Dispatchers.IO) {
            questCollection.document(keyword).get().await()
                .toObject(QuestResponse::class.java)
                ?.toDto()
        }
    }

    override suspend fun updateQuestSuccess(
        keyword: String,
        successItem: SuccessItemDto,
    ): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            val currentQuest = questCollection.document(keyword).get().await()
                .toObject(QuestResponse::class.java)
                ?: throw NoSuchElementException("Quest not found: $keyword")

            val newSuccessItem = QuestResponse.SuccessItemResponse(
                userId = successItem.userId,
                imageUrl = successItem.imageUrl,
                registerAt = successItem.registerAt,
            )
            val updatedQuest = currentQuest.copy(
                successItems = currentQuest.successItems + newSuccessItem
            )

            questCollection.document(keyword).set(updatedQuest).await()
        }
    }

    private fun QuestResponse.toDto(): QuestDto = QuestDto(
        keyword = keyWord,
        level = level,
        successItems = successItems.map { it.toDto() },
    )

    private fun QuestResponse.SuccessItemResponse.toDto(): SuccessItemDto = SuccessItemDto(
        userId = userId,
        imageUrl = imageUrl,
        registerAt = registerAt,
    )
}
