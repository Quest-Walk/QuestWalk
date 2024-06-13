package com.hapataka.questwalk.data.repository

import com.hapataka.questwalk.domain.data.remote.AuthRDS
import com.hapataka.questwalk.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Named

class AuthRepositoryImpl @Inject constructor(
    @Named("firebaseAuth")
    private val firebaseAuthRDS: AuthRDS
) : AuthRepository {
    override suspend fun registerByIdAndPw(id: String, pw: String): Result<Boolean> {
        return firebaseAuthRDS.registerByEmailAndPw(id, pw)
    }

    override suspend fun loginByIdAndPw(id: String, pw: String) :Result<Boolean> {
        return firebaseAuthRDS.loginByEmailAndPw(id, pw)
    }

    override suspend fun getCurrentUserId(): String? {
        return firebaseAuthRDS.getCurrentUserInfo()?.uid
    }

    override suspend fun logout(): Result<Unit> {
        return firebaseAuthRDS.logout()
    }
}