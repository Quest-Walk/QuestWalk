package com.hapataka.questwalk.data.firebase.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hapataka.questwalk.domain.entity.ACHIEVE_TYPE
import com.hapataka.questwalk.domain.entity.HistoryEntity
import com.hapataka.questwalk.domain.entity.HistoryEntity.AchievementEntity
import com.hapataka.questwalk.domain.entity.HistoryEntity.ResultEntity
import com.hapataka.questwalk.domain.entity.RESULT_TYPE
import com.hapataka.questwalk.domain.entity.UserEntity
import com.hapataka.questwalk.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepositoryImpl : UserRepository {
    private val remoteDb by lazy { FirebaseFirestore.getInstance() }
    private val userCollection by lazy { remoteDb.collection("user") }

    override suspend fun setUserInfo(userId: String, profileId: Int, name: String) {
        val document = userCollection.document(userId)
        val user = UserEntity(userId, name, profileId)

        document.set(user)
    }

    override suspend fun updateUserInfo(userId: String, result: HistoryEntity) {
        withContext(Dispatchers.IO) {
            val currentDocument = userCollection.document(userId)
            var currentInfo = getInfo(userId)

            if (result is ResultEntity) {
                with(currentInfo) {
                    totalDistance += result.distance
                    totalTime = if(totalTime.isEmpty()) result.time.toString() else (totalTime.toLong() + result.time).toString()
                    totalStep += result.step
                }
            }
            currentInfo.histories.add(result)
            currentDocument.set(currentInfo)
        }
    }

    override suspend fun getAllUserSize(): Long = withContext(Dispatchers.IO) {
        val documents = userCollection.get().await()

        return@withContext documents.size().toLong()
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
            histories = document.data?.get("histories") as? MutableList<Map<String, Any>> ?: mutableListOf()
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

    @Suppress("UNCHECKED_CAST")
    private fun convertToResult(item: Map<String, Any>): ResultEntity {
        with(item) {
            return ResultEntity(
                get("registerAt").toString(),
                get("quest").toString(),
                get("time").toString().toLong(),
                get("distance").toString().toFloat(),
                get("step").toString().toInt(),
                get("isFailed") as Boolean,
                convertLocationHistories(get("locations") as? List<Map<String, Any>>),
                convertLocation(get("questLocation") as? Map<String, Any>),
                get("questImg").toString(),
                RESULT_TYPE
            )
        }
    }

    private fun convertLocation(item: Map<String, Any>?): Pair<Float, Float>? {
        if (item == null) {
            return null
        }
        val latitude = item["first"].toString().toFloat()
        val longitude = item["second"].toString().toFloat()

        return Pair(latitude, longitude)
    }

    private fun convertLocationHistories(item: List<Map<String, Any>>?): List<Pair<Float, Float>>? {
        if (item == null) {
            return null
        }
        val result = mutableListOf<Pair<Float, Float>>()

        item.forEach {
            result += convertLocation(it)!!
        }
        return result
    }

    private fun convertToAchieve(item: Map<String, Any>): AchievementEntity {
        with(item) {
            return AchievementEntity(
                get("registerAt").toString(), get("achievementId").toString().toInt(), ACHIEVE_TYPE
            )
        }
    }
}
