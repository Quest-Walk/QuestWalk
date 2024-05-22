package com.hapataka.questwalk.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hapataka.questwalk.domain.repository.EncryptionKeyRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EncryptionKeyRepositoryImpl @Inject constructor(): EncryptionKeyRepository {
    private val remoteDb by lazy { FirebaseFirestore.getInstance() }
    private val collection by lazy { remoteDb.collection("EncryptionKey") }

    override suspend fun getKey(uid: String): String {
        if (uid.isNotEmpty()) {
            val document = collection.document("key_1").get().await()

            return document["value"].toString()
        }
        return ""
    }
}