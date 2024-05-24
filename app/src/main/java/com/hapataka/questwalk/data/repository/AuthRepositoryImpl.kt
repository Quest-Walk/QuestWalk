package com.hapataka.questwalk.data.repository

import com.hapataka.questwalk.domain.data.remote.AuthRDS
import com.hapataka.questwalk.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Named

class AuthRepositoryImpl @Inject constructor(
    @Named("firebaseAuth")
    private val firebaseAuthRDS: AuthRDS
) : AuthRepository {
    override suspend fun registerByIdAndPw(id: String, pw: String) {
        firebaseAuthRDS.registerByEmailAndPw(id, pw)
    }

    override suspend fun loginByIdAndPw(id: String, pw: String) {
        firebaseAuthRDS.loginByEmailAndPw(id, pw)
    }

    override suspend fun getCurrentUserInfo() {
        firebaseAuthRDS.getCurrentUserInfo()
    }
}