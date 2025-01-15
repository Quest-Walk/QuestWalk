package com.hapataka.questwalk.core.data.repository

import com.hapataka.questwalk.core.domain.repository.AuthRepository
import com.hapataka.questwalk.core.remote.api.AuthDataSource
import javax.inject.Inject
import javax.inject.Named

class DefaultAuthRepository @Inject constructor(
    @Named("FirebaseAuth") private val firebaseAuthDataSource: AuthDataSource,
) : AuthRepository {
    override suspend fun loginWithEmail(email: String, password: String): Result<String> {
        return firebaseAuthDataSource.loginWithEmail(email, password)
    }

    override suspend fun loginWithGoogle(idToken: String): Result<String> {
        return firebaseAuthDataSource.loginWithGoogle(idToken)
    }

    override fun getUserId(): String {
        return firebaseAuthDataSource.getUserId()
        // TODO: 캐시 레포 구성 후 이관해야함
    }
}