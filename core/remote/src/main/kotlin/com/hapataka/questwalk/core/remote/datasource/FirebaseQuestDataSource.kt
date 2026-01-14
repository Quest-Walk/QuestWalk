package com.hapataka.questwalk.core.remote.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.hapataka.questwalk.core.remote.api.QuestDataSource
import com.hapataka.questwalk.core.remote.model.QuestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseQuestDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) : QuestDataSource {

    private val questCollection by lazy { firestore.collection("quest") }

    override suspend fun getAllQuests(): List<QuestDto> = withContext(Dispatchers.IO) {
        val results = mutableListOf<QuestDto>()
        val levelList = intArrayOf(1, 2, 3)

        val deferredResults = levelList.map { level ->
            async {
                questCollection.whereEqualTo("level", level).get().await()
            }
        }

        deferredResults.awaitAll().forEach { snapshot ->
            results += snapshot.toObjects(QuestDto::class.java)
        }

        results
    }

    override suspend fun getQuestByKeyword(keyword: String): QuestDto? = withContext(Dispatchers.IO) {
        questCollection.document(keyword).get().await().toObject(QuestDto::class.java)
    }

    override suspend fun updateQuestSuccess(
        keyword: String,
        userId: String,
        imageUrl: String,
        registerAt: String,
    ) = withContext(Dispatchers.IO) {
        val currentQuest = getQuestByKeyword(keyword) ?: return@withContext

        val newSuccessItem = QuestDto.SuccessItemDto(userId, imageUrl, registerAt)
        val updatedQuest = currentQuest.copy(
            successItems = currentQuest.successItems + newSuccessItem
        )

        questCollection.document(keyword).set(updatedQuest).await()
    }
}
