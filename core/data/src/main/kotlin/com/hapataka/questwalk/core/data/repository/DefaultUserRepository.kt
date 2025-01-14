package com.hapataka.questwalk.core.data.repository

import com.hapataka.questwalk.core.common.model.CharacterType
import com.hapataka.questwalk.core.domain.repository.UserRepositoryNew
import com.hapataka.questwalk.core.model.User
import com.hapataka.questwalk.core.remote.api.UserDataSource
import javax.inject.Inject
import javax.inject.Named

class DefaultUserRepository @Inject constructor(
    @Named("FirestoreUser")
    private val firebaseUserDataSource: UserDataSource,
) : UserRepositoryNew {
    override suspend fun getUserInfo(userId: String): Result<User> {
        return firebaseUserDataSource.getUserInfo(userId)
    }

    override suspend fun postUserInfo(
        userId: String,
        userName: String,
        characterType: CharacterType,
    ): Result<Unit> {
        return firebaseUserDataSource.postUserInfo(
            userId = userId,
            userName = userName,
            characterType = characterType
        )
    }
}