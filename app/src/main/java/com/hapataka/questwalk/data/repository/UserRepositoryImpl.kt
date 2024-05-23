package com.hapataka.questwalk.data.repository

import com.hapataka.questwalk.data.model.UserModel
import com.hapataka.questwalk.domain.repository.UserRepo
import com.hapataka.questwalk.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userRepo: UserRepo
): UserRepository {
    override suspend fun getCurrentUser(): UserModel? {
        return null
    }
}