package com.hapataka.questwalk.core.remote.datasource

import com.google.firebase.firestore.CollectionReference
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
}