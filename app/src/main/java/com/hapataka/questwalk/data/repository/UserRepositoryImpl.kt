package com.hapataka.questwalk.data.repository

import com.hapataka.questwalk.data.model.UserModel
import com.hapataka.questwalk.domain.data.local.CacheDataSource
import com.hapataka.questwalk.domain.data.remote.UserRDS
import com.hapataka.questwalk.domain.repository.PrefDataSource
import com.hapataka.questwalk.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Named

class UserRepositoryImpl @Inject constructor(
    @Named("FirebaseUserRDS")
    private val firebaseUserRDS: UserRDS,
    private val cacheDataSource: CacheDataSource,
    private val prefDataSource: PrefDataSource
): UserRepository {
    override suspend fun getUserById(id: String): UserModel? {
        val userDTO = firebaseUserRDS.getUserById(id) ?: return null

        return UserModel(
                userDTO.id,
                userDTO.nickName,
                userDTO.characterId,
                userDTO.totalTime,
                userDTO.totalDistance,
                userDTO.totalStep
            )
    }

    override suspend fun cacheUser(user: UserModel) {
        cacheDataSource.saveUser(user)
    }

    override suspend fun uploadUser(user: UserModel) {
            firebaseUserRDS.uploadUser(user)
    }

    override suspend fun getCachedUser(): UserModel? {
        return cacheDataSource.getUser()
    }

    override suspend fun setUserIdToPref(id: String) {
        prefDataSource.setUserId(id)
    }

    override suspend fun getUserIdFromPref(): Flow<String?> {
        return prefDataSource.getUserId()
    }
}