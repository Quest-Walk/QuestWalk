package com.hapataka.questwalk.data.firebase.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.hapataka.questwalk.domain.entity.ACHIEVE_TYPE
import com.hapataka.questwalk.domain.entity.HistoryEntity
import com.hapataka.questwalk.domain.entity.HistoryEntity.*
import com.hapataka.questwalk.domain.entity.RESULT_TYPE
import com.hapataka.questwalk.domain.entity.UserEntity
import com.hapataka.questwalk.domain.repository.UserRepository
import com.hapataka.questwalk.ui.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepositoryImpl : UserRepository {
    private val remoteDb by lazy { FirebaseFirestore.getInstance() }
    private val userCollection by lazy { remoteDb.collection("user") }
    override suspend fun setInfo(userId: String, result: HistoryEntity) {
        withContext(Dispatchers.IO) {
            val currentDocument = userCollection.document(userId)
            var currentInfo = getInfo(userId)

            if (result is ResultEntity) {
                with(currentInfo) {
                    totalDistance += result.distance
                    /// TODO: time값이 Stiring이기때문에 추후에 사용한 Time의 값을 이용해 더해서 ToString 해죠야해
                    totalTime += result.time
                    totalStep += result.step
                }
            }
            currentInfo.histories.add(result)
            currentDocument.set(currentInfo)
        }
    }

    override suspend fun getInfo(userId: String): UserEntity = withContext(Dispatchers.IO) {
        val document = userCollection.document(userId).get().await()
        var nickName = ""
        var characterId = 0
        var totalTime = ""
        var totalDistance = 0f
        var totalStep = 0L
        var histories = mutableListOf<Map<String, Any>>()

        if (document.exists()) {
            nickName = document.data?.get("nickName").toString()
            characterId = document.data?.get("characterId").toString().toInt()
            totalTime = document.data?.get("totalTime").toString()
            totalDistance = document.data?.get("totalDistance").toString().toFloat()
            totalStep = document.data?.get("totalStep").toString().toLong()
            histories = document.data?.get("histories") as MutableList<Map<String, Any>>
        }
        return@withContext UserEntity(
            userId,
            nickName,
            characterId,
            totalTime,
            totalDistance,
            totalStep,
            convertToHistories(histories)
        )
    }

    override suspend fun getUserHistory(userId: String): MutableList<HistoryEntity> =
        withContext(Dispatchers.IO) {
            val currentUserInfo = getInfo(userId)

            return@withContext currentUserInfo.histories
        }


    override suspend fun getAchieveHistory(userId: String): MutableList<AchievementEntity> =
        withContext(Dispatchers.IO) {
            val currentUserInfo = getInfo(userId)

            return@withContext currentUserInfo.histories.filterIsInstance<AchievementEntity>() as MutableList<AchievementEntity>
        }

    override suspend fun getResultHistory(userId: String): MutableList<ResultEntity> =
        withContext(Dispatchers.IO) {
            val currentUserInfo = getInfo(userId)

            return@withContext currentUserInfo.histories.filterIsInstance<ResultEntity>() as MutableList<ResultEntity>
        }

    private fun convertToHistories(items: List<Map<String, Any>>): MutableList<HistoryEntity> {
        var resultList = mutableListOf<HistoryEntity>()



        items.forEach { item ->
            when (item["type"]) {
                RESULT_TYPE -> {
                    resultList += convertToResult(item)
                }

                ACHIEVE_TYPE -> {
                    resultList += convertToAchieve(item)
                }
            }
        }
        return resultList
    }

    private fun convertToResult(item: Map<String, Any>): ResultEntity {
        with(item) {
            return ResultEntity(
                get("registerAt").toString(),
                get("quest").toString(),
                get("time").toString(),
                get("distance").toString().toFloat(),
                get("step").toString().toInt(),
                get("isFailed") as Boolean,
                get("longitudes") as List<Float>,
                get("latitueds") as List<Float>,
                get("questLongitude").toString().toFloat(),
                get("questLatitued").toString().toFloat(),
                get("questImg").toString(),
                RESULT_TYPE
            )
        }
    }

    private fun convertToAchieve(item: Map<String, Any>): AchievementEntity {
        with(item) {
            return AchievementEntity(
                get("registerAt").toString(), get("achievementId").toString().toInt(), ACHIEVE_TYPE
            )
        }
    }
}
