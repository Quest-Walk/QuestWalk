package com.hapataka.questwalk.core.remote.datasource

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.hapataka.questwalk.core.model.CharacterType
import com.hapataka.questwalk.core.model.User
import com.hapataka.questwalk.core.remote.api.UserDataSource
import com.hapataka.questwalk.core.remote.model.UserDefaultInfoDto
import com.hapataka.questwalk.core.remote.model.toModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseUserDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userCollection: CollectionReference,
) : UserDataSource {
    override suspend fun getUserInfo(userId: String): Result<User> {
        return kotlin.runCatching {
            val userDocument = userCollection.document(userId)
            val userDefaultInfo =
                userDocument.get().await().toObject(UserDefaultInfoDto::class.java)
                    ?: throw IllegalStateException("유저를 찾을 수 없습니다.")

            userDefaultInfo.toModel(
                successKeywords = userDocument.getSuccessKeywords(),
                achievementIds = userDocument.getAchievements()
            )
        }
    }

    private suspend fun DocumentReference.getSuccessKeywords(): List<String> {
        return this.collection("successKeywords").get()
            .await()
            .documents
            .mapNotNull { it.getString("keyword") }
    }

    private suspend fun DocumentReference.getAchievements(): List<Int> {
        return this.collection("achievements").get()
            .await()
            .documents
            .mapNotNull { it.getLong("achievementId")?.toInt() }
    }

    override suspend fun postUserInfo(
        userId: String,
        userName: String,
        characterType: CharacterType,
    ): Result<Unit> {
        return kotlin.runCatching {
            userCollection
                .document(userId)
                .set(
                    UserDefaultInfoDto(
                        userId = userId,
                        userName = userName,
                        characterId = characterType.id
                    )
                )
                .await()
        }
    }

    override suspend fun getLastAggregatedAt(userId: String): String {
        return userCollection.document(userId)
            .get()
            .await()
            .getString(FIELD_LAST_AGGREGATED_AT)
            .orEmpty()
    }

    override suspend fun setUserAggregate(
        userId: String,
        totalTime: Long,
        totalDistance: Float,
        totalStep: Long,
        successKeywords: List<String>,
        achievementIds: List<Int>,
        lastAggregatedAt: String,
    ) {
        val userDocument = userCollection.document(userId)

        // 문서 ID가 키워드/업적 ID라 같은 값을 다시 써도 중복이 생기지 않는다
        if (successKeywords.isNotEmpty() || achievementIds.isNotEmpty()) {
            val batch = firestore.batch()
            successKeywords.forEach { keyword ->
                batch.set(
                    userDocument.collection("successKeywords").document(keyword),
                    hashMapOf("keyword" to keyword)
                )
            }
            achievementIds.forEach { achievementId ->
                batch.set(
                    userDocument.collection("achievements").document(achievementId.toString()),
                    hashMapOf("achievementId" to achievementId)
                )
            }
            batch.commit().await()
        }

        // 절대값 덮어쓰기라 읽기-수정-쓰기 경합이 없다
        userDocument.set(
            hashMapOf(
                "totalTime" to totalTime,
                "totalDistance" to totalDistance,
                "totalStep" to totalStep,
                FIELD_LAST_AGGREGATED_AT to lastAggregatedAt,
            ),
            SetOptions.merge()
        ).await()
    }

    companion object {
        private const val FIELD_LAST_AGGREGATED_AT = "lastAggregatedAt"
    }
}
