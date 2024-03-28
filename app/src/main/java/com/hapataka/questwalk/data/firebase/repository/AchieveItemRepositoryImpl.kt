package com.hapataka.questwalk.data.firebase.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hapataka.questwalk.domain.entity.AchieveItemEntity
import com.hapataka.questwalk.domain.repository.AchieveItemRepository
import kotlinx.coroutines.tasks.await

class AchieveItemRepositoryImpl : AchieveItemRepository {
    private val remoteDb by lazy { FirebaseFirestore.getInstance() }
    private val collection by lazy { remoteDb.collection("AchieveItem") }
    override suspend fun getAchieveItem(): List<AchieveItemEntity> {
        val documents = collection.get().await().documents
        val results = documents.map {
                it?.toObject(AchieveItemEntity::class.java) ?: return emptyList()
        }

        return results
    }
}