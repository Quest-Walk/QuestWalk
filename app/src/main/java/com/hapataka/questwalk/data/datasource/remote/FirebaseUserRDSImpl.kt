package com.hapataka.questwalk.data.datasource.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.hapataka.questwalk.data.dto.UserDTO
import com.hapataka.questwalk.domain.data.remote.UserRDS
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseUserRDSImpl @Inject constructor() : UserRDS {
    private val remoteDB by lazy { FirebaseFirestore.getInstance() }
    private val userDB by lazy { remoteDB.collection("users") }

    suspend fun getUserById(userId: String): UserDTO? {
        val userInfo = kotlin.runCatching {
            userDB.document(userId).get().await().toObject(UserDTO::class.java)
        }
                return userInfo.getOrNull()
    }
}