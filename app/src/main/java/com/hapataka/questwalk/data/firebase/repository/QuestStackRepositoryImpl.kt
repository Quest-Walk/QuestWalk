package com.hapataka.questwalk.data.firebase.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.hapataka.questwalk.domain.entity.QuestStackEntity
import com.hapataka.questwalk.domain.entity.QuestStackEntity.*
import com.hapataka.questwalk.domain.repository.QuestStackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalTime

class QuestStackRepositoryImpl : QuestStackRepository {
    private val remoteDb by lazy { FirebaseFirestore.getInstance() }
    private val questCollection by lazy { remoteDb.collection("quest") }

    override suspend fun updateQuest(keyword: String, userId: String, imageUrl: String, registerAt: String) {
        withContext(Dispatchers.IO) {
            var currentItem = getItemByKeyword(keyword)

            currentItem.successItems += SuccessItem(userId, imageUrl, registerAt)
            questCollection.document(keyword)
                .set(currentItem)
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
            val allItems = questCollection.get().await().documents

            allItems.forEach { document ->
                results += document.toObject(QuestStackEntity::class.java)!!
            }
            return@withContext results
        }
}