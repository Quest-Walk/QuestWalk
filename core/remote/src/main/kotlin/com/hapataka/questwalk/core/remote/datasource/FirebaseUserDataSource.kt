package com.hapataka.questwalk.core.remote.datasource

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.hapataka.questwalk.core.model.CharacterType
import com.hapataka.questwalk.core.model.User
import com.hapataka.questwalk.core.remote.api.UserDataSource
import com.hapataka.questwalk.core.remote.model.UserDefaultInfoDto
import com.hapataka.questwalk.core.remote.model.getDefaultInfo
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
        }
    }

    override suspend fun updateUserInfo(user: User) {
        val batch = firestore.batch()
        val userDefaultInfo = user.getDefaultInfo()
        val userDocument = userCollection.document(user.userId)

        firestore.runTransaction { transaction ->
            val keywordSnapshots = user.successKeywords.map { keyword ->
                val keywordDocument = userDocument.collection("successKeywords").document(keyword)
                keyword to transaction.get(keywordDocument)
            }
            val achievementSnapshots = user.achievementIds.map { achievementId ->
                val achievementDocument =
                    userDocument.collection("achievements").document(achievementId.toString())
                achievementId to transaction.get(achievementDocument)
            }

            keywordSnapshots.forEach { (keyword, snapshot) ->
                if (snapshot.exists().not()) {
                    batch.set(
                        userDocument.collection("successKeywords").document(keyword),
                        hashMapOf("keyword" to keyword)
                    )
                }
            }
            achievementSnapshots.forEach { (achievementId, snapshot) ->
                if (snapshot.exists().not()) {
                    batch.set(
                        userDocument.collection("achievements").document(achievementId.toString()),
                        hashMapOf("achievementId" to achievementId)
                    )
                }
            }
            batch.update(userDocument, userDefaultInfo)
        }

        batch.commit()
    }
}