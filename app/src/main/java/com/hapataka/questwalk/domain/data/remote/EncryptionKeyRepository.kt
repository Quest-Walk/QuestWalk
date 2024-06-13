package com.hapataka.questwalk.domain.data.remote

interface EncryptionKeyRepository {
    suspend fun getKey(uid: String): String
}