package com.hapataka.questwalk.core.remote.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.hapataka.questwalk.core.remote.api.AchieveItemDataSource
import com.hapataka.questwalk.core.remote.model.AchieveItemDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAchieveItemDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) : AchieveItemDataSource {

    private val collection by lazy { firestore.collection("AchieveItem") }

    override suspend fun getAchieveItems(): List<AchieveItemDto> {
        val documents = collection.get().await().documents
        return documents.mapNotNull { document ->
            document?.toObject(AchieveItemDto::class.java)
        }
    }
}
