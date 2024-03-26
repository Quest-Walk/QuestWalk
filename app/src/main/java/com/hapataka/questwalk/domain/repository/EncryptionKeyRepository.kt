package com.hapataka.questwalk.domain.repository

interface EncryptionKeyRepository {
    suspend fun getKey(uid: String): String
}