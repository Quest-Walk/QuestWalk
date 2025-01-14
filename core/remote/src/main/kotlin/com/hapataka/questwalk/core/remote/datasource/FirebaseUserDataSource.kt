package com.hapataka.questwalk.core.remote.datasource

import com.google.firebase.firestore.CollectionReference
import com.hapataka.questwalk.core.common.model.CharacterType
import com.hapataka.questwalk.core.model.User
import com.hapataka.questwalk.core.remote.api.UserDataSource
import com.hapataka.questwalk.core.remote.model.UserDto
import com.hapataka.questwalk.core.remote.model.toModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseUserDataSource @Inject constructor(
    private val userCollection: CollectionReference,
) : UserDataSource {
    override suspend fun getUserInfo(userId: String): Result<User> {
        return kotlin.runCatching {
            userCollection
                .document(userId)
                .get()
                .await()
                .toObject(UserDto::class.java)!!
                .toModel()
        }
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
                    UserDto(
                        userId = userId,
                        userName = userName,
                        characterId = characterType.id
                    )
                )
        }
    }
}