package com.hapataka.questwalk.core.data.repository

import com.hapataka.questwalk.core.domain.repository.AuthRepository
import com.hapataka.questwalk.core.remote.api.AuthDataSource
import javax.inject.Inject
import javax.inject.Named

class DefaultAuthRepository @Inject constructor(
    @Named("FirebaseAuth") private val googleAuthDataSource: AuthDataSource,
) : AuthRepository {
    override suspend fun loginWithEmail(email: String, password: String): Result<String> {
        return googleAuthDataSource.loginWithEmail(email, password)
    }
}