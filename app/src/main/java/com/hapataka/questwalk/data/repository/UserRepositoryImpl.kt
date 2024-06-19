package com.hapataka.questwalk.data.repository

import com.hapataka.questwalk.data.model.UserModel
import com.hapataka.questwalk.domain.data.remote.UserRDS
import com.hapataka.questwalk.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Named

class UserRepositoryImpl @Inject constructor(
    @Named("FirebaseUserRDS")
    private val firebaseUserRDS: UserRDS
    ) : UserRepository {
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

    override suspend fun uploadUser(user: UserModel) {
        firebaseUserRDS.uploadUser(user)
    }

    override suspend fun deleteUserById(userId: String) {
        firebaseUserRDS.deleteUserById(userId)
    }
}