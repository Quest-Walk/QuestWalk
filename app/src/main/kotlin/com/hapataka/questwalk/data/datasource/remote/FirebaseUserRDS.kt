package com.hapataka.questwalk.data.datasource.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.hapataka.questwalk.core.remote.model.UserDefaultInfoDto
import com.hapataka.questwalk.data.model.UserModel
import com.hapataka.questwalk.domain.data.remote.UserRDS
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseUserRDS @Inject constructor() : UserRDS {
    private val remoteDB by lazy { FirebaseFirestore.getInstance() }
    private val userDB by lazy { remoteDB.collection("users") }

    override suspend fun getUserById(userId: String): UserDefaultInfoDto? {
        val userInfo = kotlin.runCatching {
            userDB.document(userId).get().await().toObject(UserDefaultInfoDto::class.java)
        }

        return userInfo.getOrNull()
    }

    override suspend fun uploadUser(user: UserModel) {
        userDB.document(user.userId).set(user)
    }

    override suspend fun deleteUserById(userId: String) {
        userDB.document(userId).delete()
    }
}