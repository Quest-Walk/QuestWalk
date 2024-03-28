package com.hapataka.questwalk.data.firebase.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hapataka.questwalk.domain.entity.AchieveStackEntity
import com.hapataka.questwalk.domain.repository.AchieveStackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AchieveStackRepositoryImpl : AchieveStackRepository {
    private val remoteDb by lazy { FirebaseFirestore.getInstance() }
    private val collection by lazy { remoteDb.collection("AchieveStack") }

    override suspend fun countUpAchieveStack(id: Int) {
        val currentItem = getAchieveStateById(id)

        currentItem.count++
        collection.document("$id")
            .set(currentItem)
    }

    override suspend fun getAchieveStateById(id: Int): AchieveStackEntity =
        withContext(Dispatchers.IO) {
            val document = collection.document("$id")
            val achieveDocument = document.get().await()

            achieveDocument.toObject(AchieveStackEntity::class.java)!!
        }
}
