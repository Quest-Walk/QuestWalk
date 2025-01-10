package com.hapataka.questwalk.core.data.repository

import com.hapataka.questwalk.core.domain.repository.AuthRepository
import com.hapataka.questwalk.core.remote.api.AuthDataSource
import javax.inject.Inject
import javax.inject.Named

class DefaultAuthRepository @Inject constructor(
    @Named("Firebase") private val googleAuthDataSource: AuthDataSource
) : AuthRepository {
    override suspend fun loginWithEmail(email: String, password: String): Result<Unit> {
        return googleAuthDataSource.loginWithEmail(email, password)
    }
}