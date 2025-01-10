package com.hapataka.questwalk.data.datasource.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.hapataka.questwalk.data.dto.ACHIEVEMENT_RECORD
import com.hapataka.questwalk.data.dto.AchievementRecordDTO
import com.hapataka.questwalk.data.dto.HistoryResultDTO
import com.hapataka.questwalk.data.dto.RESULT_RECORD
import com.hapataka.questwalk.data.dto.ResultRecordDTO
import com.hapataka.questwalk.domain.data.remote.HistoryRDS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseHistoryRDS @Inject constructor() : HistoryRDS {
    private val remoteDB by lazy { FirebaseFirestore.getInstance() }
    private val historyDB by lazy { remoteDB.collection("histories") }

    override suspend fun getHistoriesById(id: String): HistoryResultDTO =
        withContext(Dispatchers.IO) {
            val resultDeferred =
                async {
                    historyDB.whereEqualTo("userId", id)
                        .whereEqualTo("recordType", RESULT_RECORD).get().await()
                }
            val achievementDeferred =
                async {
                    historyDB.whereEqualTo("userId", id)
                        .whereEqualTo("recordType", ACHIEVEMENT_RECORD).get().await()
                }

            return@withContext HistoryResultDTO(
                resultDeferred.await().map { it.toObject(ResultRecordDTO::class.java) },
                achievementDeferred.await().map { it.toObject(AchievementRecordDTO::class.java) }
            )
        }

    override suspend fun uploadHistoryInfo(history: Any) {
        when (history) {
            is ResultRecordDTO -> {
                historyDB.add(history).await()
            }

            is AchievementRecordDTO -> {
                historyDB.add(history).await()
            }

            else -> throw (Exception("Unknown record type"))
        }
    }


    override suspend fun deleteHistoriesById(id: String): Result<Unit> {
        return kotlin.runCatching {
            historyDB.whereEqualTo("userId", id).get().await().documents
                .forEach { historyDB.document(it.id).delete().await() }
        }
    }
}