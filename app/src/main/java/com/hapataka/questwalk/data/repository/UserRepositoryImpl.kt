package com.hapataka.questwalk.data.repository

import com.google.firebase.auth.FirebaseUser
import com.hapataka.questwalk.data.model.UserModel
import com.hapataka.questwalk.domain.data.local.CacheDataSource
import com.hapataka.questwalk.domain.data.remote.UserRDS
import com.hapataka.questwalk.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Named

class UserRepositoryImpl @Inject constructor(
    @Named("FirebaseUserRDS")
    private val firebaseUserRDS: UserRDS,
    private val cacheDataSource: CacheDataSource
): UserRepository {
    override suspend fun getCurrentUser(): UserModel? {
        return null
    }

    override suspend fun cacheUser(user: UserModel) {
        cacheDataSource.saveUser(user)
    }

    override suspend fun uploadUser(user: UserModel) {
        firebaseUserRDS.setUserById(user)
    }
}