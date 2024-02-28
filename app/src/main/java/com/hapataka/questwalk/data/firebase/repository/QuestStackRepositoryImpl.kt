package com.hapataka.questwalk.data.firebase.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hapataka.questwalk.domain.entity.QuestStackEntity
import com.hapataka.questwalk.domain.entity.QuestStackEntity.*
import com.hapataka.questwalk.domain.repository.QuestStackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class QuestStackRepositoryImpl : QuestStackRepository {
    private val remoteDb by lazy { FirebaseFirestore.getInstance() }
    private val questCollection by lazy { remoteDb.collection("quest") }

    override suspend fun updateQuest(keyword: String, userId: String, imageUrl: String) {
        withContext(Dispatchers.IO) {
            var currentItem = getItemByKeyword(keyword)

            currentItem.successItems += SuccessItem(userId, imageUrl)
            questCollection.document(keyword)
                .set(currentItem)
        }
    }

    override suspend fun getItemByKeyword(keyword: String): QuestStackEntity =
        withContext(Dispatchers.IO) {
            val document = questCollection.document(keyword)
            val keywordDocument = document.get().await()
            var level = 1
            val successItems = mutableListOf<SuccessItem>()

            if (keywordDocument.exists()) {
                val currentItems = keywordDocument.data?.get("successItems") as Map<String, String>

                currentItems.forEach {
                    successItems += SuccessItem(it.key, it.value)
                }
                level = keywordDocument.data?.get("level").toString().toInt()
            }
            return@withContext QuestStackEntity(keyword, level, successItems)
        }

    override suspend fun getAllItems(): List<QuestStackEntity> =
        withContext(Dispatchers.IO) {
            val results = mutableListOf<QuestStackEntity>()
            val allItems = questCollection.get().await().documents

            allItems.forEach { document ->
                val successItems = mutableListOf<SuccessItem>()
                val keyword = document["keyWord"].toString()
                val level = document["level"].toString().toInt()
                val currentItems = document["successItems"] as Map<String, String>

                currentItems.forEach {
                    successItems += SuccessItem(it.key, it.value)
                }
                results += QuestStackEntity(keyword, level, successItems)
            }
            return@withContext results
        }
}