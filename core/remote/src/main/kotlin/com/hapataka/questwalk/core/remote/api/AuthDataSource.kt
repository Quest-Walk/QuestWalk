package com.hapataka.questwalk.core.remote.api

interface AuthDataSource {
    suspend fun loginWithEmail(email: String, password: String): Result<String>
}