package com.hapataka.questwalk.data.firebase.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hapataka.questwalk.domain.entity.AchieveStackEntity
import com.hapataka.questwalk.domain.repository.AchieveStackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AchieveStackRepositoryImpl : AchieveStackRepository {
    val achievDb by lazy { FirebaseFirestore.getInstance() }
    val achievCollection by lazy { achievDb.collection("Achievement") }

    override suspend fun countUpAchieveStack(id: Int) {
        val currentItem = getAchieveStateById(id)

        currentItem.count++
        achievCollection.document("$id")
            .set(currentItem)
    }

    override suspend fun getAchieveStateById(id: Int): AchieveStackEntity =
        withContext(Dispatchers.IO) {
            val document = achievCollection.document("$id")
            val achieveDocument = document.get().await()
            val achieveId = achieveDocument.data?.get("achievementId").toString().toInt()
            val count = achieveDocument.data?.get("count").toString().toInt()

            return@withContext AchieveStackEntity(achieveId, count)
        }
}
