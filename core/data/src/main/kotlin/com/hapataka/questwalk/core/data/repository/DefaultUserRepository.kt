package com.hapataka.questwalk.core.data.repository

import com.hapataka.questwalk.core.domain.repository.UserRepositoryNew
import com.hapataka.questwalk.core.local.api.LocalUserDataSource
import com.hapataka.questwalk.core.model.CharacterType
import com.hapataka.questwalk.core.model.User
import com.hapataka.questwalk.core.remote.api.AuthDataSource
import com.hapataka.questwalk.core.remote.api.UserDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class DefaultUserRepository @Inject constructor(
    @Named("FirestoreUser")
    private val firebaseUserDataSource: UserDataSource,
    @Named("FirebaseAuth")
    private val firebaseAuthDataSource: AuthDataSource,
    private val localUserDataSource: LocalUserDataSource,
) : UserRepositoryNew {
    override suspend fun checkUserLoggedIn() = withContext(Dispatchers.IO) {
        localUserDataSource.existLoginUser()
    }

    override suspend fun insertUser(userId: String) = withContext(Dispatchers.IO) {
        localUserDataSource.insertUser(User(userId = userId))
    }

    override suspend fun updateUserInfo(
        time: Long,
        distance: Float,
        step: Long,
        keyword: String,
        achievementId: Int?,
    ) = withContext(Dispatchers.IO) {
        val user = localUserDataSource.getCurrentUser().first()
        val new = if (achievementId != null) {
            user.copy(
                totalTime = user.totalTime + time,
                totalDistance = user.totalDistance + distance,
                totalStep = user.totalStep + step,
                successKeywords = user.successKeywords + keyword,
                achievementIds = user.achievementIds + achievementId
            )
        } else {
            user.copy(
                totalTime = user.totalTime + time,
                totalDistance = user.totalDistance + distance,
                totalStep = user.totalStep + step,
                successKeywords = user.successKeywords + keyword
            )
        }
        localUserDataSource.updateUser(new)
        firebaseUserDataSource.updateUserInfo(new)
    }

    override fun getUserInfo(): Flow<User> {
        return localUserDataSource.getCurrentUser()
    }

    override suspend fun clearUserInfo() = withContext(Dispatchers.IO) {
        localUserDataSource.clearUsers()
    }

    override suspend fun fetchUserInfo(): Result<Unit> {
        return kotlin.runCatching {
            localUserDataSource.getCurrentUser().first().let { user ->
                firebaseUserDataSource.getUserInfo(user.userId)
                    .onSuccess { userInfo -> localUserDataSource.insertUser(userInfo) }
                    .onFailure { e -> throw e }
            }
        }
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