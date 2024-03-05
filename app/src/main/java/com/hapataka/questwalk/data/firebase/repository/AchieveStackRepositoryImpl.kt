package com.hapataka.questwalk.data.firebase.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.hapataka.questwalk.domain.entity.AchieveStackEntity
import com.hapataka.questwalk.domain.repository.AchieveStackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AchieveStackRepositoryImpl : AchieveStackRepository {
    private val remoteDb by lazy { FirebaseFirestore.getInstance() }
    private val achieveCollection by lazy { remoteDb.collection("AchieveItem") }

    override suspend fun countUpAchieveStack(id: Int) {
        val currentItem = getAchieveStateById(id)

        currentItem.count++
        achieveCollection.document("$id")
            .set(currentItem)
    }

    override suspend fun getAchieveStateById(id: Int): AchieveStackEntity =
        withContext(Dispatchers.IO) {
            val document = achieveCollection.document("$id")
            val achieveDocument = document.get().await()

            return@withContext achieveDocument.toObject(AchieveStackEntity::class.java)!!
        }
}
