package com.hapataka.questwalk.data.datasource.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.hapataka.questwalk.data.dto.AchievementItemDTO
import com.hapataka.questwalk.domain.data.remote.AchievementsDataSource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAchievementsDataSource @Inject constructor() : AchievementsDataSource {
    private val remoteDB by lazy { FirebaseFirestore.getInstance() }
    private val historyDB by lazy { remoteDB.collection("AchieveItem") }

    override suspend fun getAchievements(): List<AchievementItemDTO> {
        val items = historyDB.get().await()

        return items.map { it.toObject(AchievementItemDTO::class.java) }
    }
}