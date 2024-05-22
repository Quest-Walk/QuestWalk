package com.hapataka.questwalk.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hapataka.questwalk.domain.entity.QuestStackEntity
import com.hapataka.questwalk.domain.entity.QuestStackEntity.SuccessItem
import com.hapataka.questwalk.domain.repository.QuestStackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class QuestStackRepositoryImpl @Inject constructor() : QuestStackRepository {
    private val remoteDb by lazy { FirebaseFirestore.getInstance() }
    private val questCollection by lazy { remoteDb.collection("quest") }

    override suspend fun updateQuest(
        keyword: String,
        userId: String,
        imageUrl: String,
        registerAt: String
    ) {
        withContext(Dispatchers.IO) {
            val currentItem = getItemByKeyword(keyword)

            currentItem.successItems += SuccessItem(userId, imageUrl, registerAt)
            questCollection.document(keyword).set(currentItem).await()
        }
    }

    override suspend fun getItemByKeyword(keyword: String): QuestStackEntity =
        withContext(Dispatchers.IO) {
            val document = questCollection.document(keyword)
            val keywordDocument = document.get().await()

            return@withContext keywordDocument.toObject(QuestStackEntity::class.java)!!
        }

    override suspend fun getAllItems(): List<QuestStackEntity> =
        withContext(Dispatchers.IO) {
            val results = mutableListOf<QuestStackEntity>()
            val levelList = intArrayOf(1, 2, 3)
            val differ = levelList.map {
                async {
                    questCollection.whereEqualTo("level", it).get().await()
                }
            }

            differ.awaitAll().forEach {
                results += it.toObjects(QuestStackEntity::class.java)
            }
            return@withContext results
        }
}