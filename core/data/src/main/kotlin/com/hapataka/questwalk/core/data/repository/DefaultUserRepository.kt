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

    override fun getUserInfo(): Flow<User?> {
        return localUserDataSource.getCurrentUser()
    }

    override suspend fun clearUserInfo() = withContext(Dispatchers.IO) {
        localUserDataSource.clearUsers()
    }

    override suspend fun fetchUserInfo(): Result<Unit> {
        return kotlin.runCatching {
            val user = localUserDataSource.getCurrentUser().first()
                ?: throw IllegalStateException("No user found in local DB")
            firebaseUserDataSource.getUserInfo(user.userId)
                .onSuccess { userInfo -> localUserDataSource.insertUser(userInfo) }
                .onFailure { e -> throw e }
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

    override suspend fun getLastAggregatedAt(userId: String): Result<String> =
        withContext(Dispatchers.IO) {
            kotlin.runCatching { firebaseUserDataSource.getLastAggregatedAt(userId) }
        }

    override suspend fun applyAggregate(
        userId: String,
        totalTime: Long,
        totalDistance: Float,
        totalStep: Long,
        successKeywords: List<String>,
        achievementIds: List<Int>,
        lastAggregatedAt: String,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        kotlin.runCatching {
            firebaseUserDataSource.setUserAggregate(
                userId = userId,
                totalTime = totalTime,
                totalDistance = totalDistance,
                totalStep = totalStep,
                successKeywords = successKeywords,
                achievementIds = achievementIds,
                lastAggregatedAt = lastAggregatedAt,
            )

            // 원격 반영이 끝난 뒤에만 로컬 캐시를 맞춘다
            val cached = localUserDataSource.getCurrentUser().first()
            if (cached != null) {
                localUserDataSource.updateUser(
                    cached.copy(
                        totalTime = totalTime,
                        totalDistance = totalDistance,
                        totalStep = totalStep,
                        successKeywords = (cached.successKeywords + successKeywords).distinct(),
                        achievementIds = (cached.achievementIds + achievementIds).distinct(),
                    )
                )
            }
        }
    }
}
