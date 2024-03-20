package com.hapataka.questwalk.data.firebase.repository

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hapataka.questwalk.domain.entity.ACHIEVE_TYPE
import com.hapataka.questwalk.domain.entity.HistoryEntity
import com.hapataka.questwalk.domain.entity.HistoryEntity.AchieveResultEntity
import com.hapataka.questwalk.domain.entity.HistoryEntity.ResultEntity
import com.hapataka.questwalk.domain.entity.RESULT_TYPE
import com.hapataka.questwalk.domain.entity.UserEntity
import com.hapataka.questwalk.domain.repository.UserRepository
import com.hapataka.questwalk.ui.record.TAG
import com.hapataka.questwalk.util.extentions.decryptECB
import com.hapataka.questwalk.util.extentions.encryptECB
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

    override suspend fun updateHistoryInfo(userId: String, result: HistoryEntity) {
        withContext(Dispatchers.IO) {
            val currentDocument = userCollection.document(userId)
            var currentInfo = getInfo(userId)
            var userStack = hashMapOf<String, Any>()

            Log.i(TAG, "currentInfo: $currentInfo")
            Log.i(TAG, "result: $result")

            if (result is ResultEntity) {
                userStack = hashMapOf(
                    "histories" to FieldValue.arrayUnion(covertToUploadObject(result)),
                    "totalDistance" to currentInfo.totalDistance + result.distance,
                    "totalStep" to currentInfo.totalStep + result.step,
                    "totalTime" to currentInfo.totalTime.toLong() + result.time
                )
                Log.d(TAG, "userStack: $userStack")
            }

            if (result is AchieveResultEntity) {
                userStack = hashMapOf("histories" to FieldValue.arrayUnion(result))
            }
            currentDocument.update(userStack)
        }
    }

    override suspend fun getAllUserSize(): Long = withContext(Dispatchers.IO) {
        val documents = userCollection.get().await()

        documents.size().toLong()
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
            nickName = document.getWithKey("nickName")
            characterId = document.getWithKey("characterId").toInt()
            totalTime = document.getWithKey("totalTime")
            totalDistance = document.getWithKey("totalDistance").toFloat()
            totalStep = document.getWithKey("totalStep").toLong()
            histories =
                document.data?.get("histories") as? MutableList<Map<String, Any>> ?: mutableListOf()
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

    private fun DocumentSnapshot.getWithKey(key: String): String {
        return this.data?.get(key).toString()
    }

    override suspend fun getUserHistory(userId: String): MutableList<HistoryEntity> =
        withContext(Dispatchers.IO) {
            val currentUserInfo = getInfo(userId)

            return@withContext currentUserInfo.histories
        }

    override suspend fun getAchieveHistory(userId: String): MutableList<AchieveResultEntity> =
        withContext(Dispatchers.IO) {
            val currentUserInfo = getInfo(userId)

            return@withContext currentUserInfo.histories.filterIsInstance<AchieveResultEntity>() as MutableList<AchieveResultEntity>
        }

    override suspend fun getResultHistory(userId: String): MutableList<ResultEntity> =
        withContext(Dispatchers.IO) {
            val currentUserInfo = getInfo(userId)

            return@withContext currentUserInfo.histories.filterIsInstance<ResultEntity>() as MutableList<ResultEntity>
        }

    override suspend fun deleteUserData(userId: String) {
        withContext(Dispatchers.IO) {
            val document = userCollection.document(userId)

            document.delete()
        }
    }

    private fun covertToUploadObject(result: ResultEntity): ResultEntityUploadObject {
        val locations = Gson().toJson(result.locations)
        val questLocation = if (result.questLocation != null) {
            Gson().toJson(result.questLocation)
        } else {
            null
        }

        return ResultEntityUploadObject(
            result.registerAt,
            result.quest,
            result.time.toString(),
            result.distance,
            result.step,
            result.isSuccess,
            locations.encryptECB(),
            questLocation?.encryptECB(),
            result.questImg
        )
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
            val dto = ResultEntityUploadObject(
                get("registerAt").toString(),
                get("quest").toString(),
                get("time").toString(),
                get("distance").toString().toFloat(),
                get("step").toString().toLong(),
                get("isSuccess") as Boolean,
                get("locations").toString(),
                get("questLocation").toString(),
                get("questImg").toString(),
            )
            val locationsJson = dto.locations.decryptECB()
            val locations: MutableList<Pair<Float, Float>> =
                Gson().fromJson(
                    locationsJson,
                    object : TypeToken<MutableList<Pair<Float, Float>>>() {}.type
                )
            val questLocationJson = dto.questLocation
            var questLocation: Pair<Float, Float>? = null

            if (questLocationJson != null && questLocationJson != "null") {
                val type = object : TypeToken<Pair<Float, Float>>() {}.type
                questLocation = Gson().fromJson(questLocationJson.decryptECB(), type)
            }

            return ResultEntity(
                dto.registerAt,
                dto.quest,
                dto.time.toLong(),
                dto.distance,
                dto.step,
                dto.isSuccess,
                locations,
                questLocation,
                dto.questImg,
                RESULT_TYPE
            )
        }
    }

    private fun convertToAchieve(item: Map<String, Any>): AchieveResultEntity {
        with(item) {
            return AchieveResultEntity(
                get("registerAt").toString(), get("achievementId").toString().toInt(), ACHIEVE_TYPE
            )
        }
    }

    data class ResultEntityUploadObject(
        val registerAt: String = "",
        val quest: String = "",
        val time: String = "",
        val distance: Float = 0f,
        val step: Long = 0L,
        @JvmField
        val isSuccess: Boolean = false,
        val locations: String = "",
        val questLocation: String? = null,
        val questImg: String? = null,
        val type: String = RESULT_TYPE
    )
}
