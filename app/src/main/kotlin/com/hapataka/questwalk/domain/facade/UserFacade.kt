package com.hapataka.questwalk.domain.facade

import com.hapataka.questwalk.core.domain.usecase.GetLoginUserIdUseCase
import com.hapataka.questwalk.core.domain.usecase.GetUserInfoUseCase
import com.hapataka.questwalk.data.model.UserModel
import com.hapataka.questwalk.domain.usecase.CacheCurrentUserUserCase
import com.hapataka.questwalk.domain.usecase.CheckCurrentUserNameUseCase
import com.hapataka.questwalk.domain.usecase.GetCacheUserUseCase
import com.hapataka.questwalk.domain.usecase.UpdateUserNameUseCase
import com.hapataka.questwalk.domain.usecase.UploadUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject


class UserFacade @Inject constructor(
    private val cacheCurrentUserUseCase: CacheCurrentUserUserCase,
    private val getCacheUserUseCase: GetCacheUserUseCase,
    private val checkCurrentUserNameUseCase: CheckCurrentUserNameUseCase,
    private val updateUserNameUseCase: UpdateUserNameUseCase,
    private val uploadUserUseCase: UploadUserUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getLoginUserIdUseCase: GetLoginUserIdUseCase,
) {
    suspend fun cacheAndGetCurrentUser(): UserModel? = withContext(Dispatchers.IO) {
        async { cacheCurrentUserUseCase() }.await()

        return@withContext getCacheUserUseCase()
    }

    suspend fun checkCurrentUserName(): Boolean {
        return checkCurrentUserNameUseCase()
    }

    suspend fun updateUserName(newName: String) {
        val user = updateUserNameUseCase(newName)

        if (user != null) {
            uploadUserUseCase(user)
        }
    }

    suspend fun getUserInfo(): UserModel {
        val cacheUser = getCacheUserUseCase()

        if (cacheUser != null) return cacheUser

        val userId = getLoginUserIdUseCase().getOrThrow()
        val userInfo = getUserInfoUseCase(userId = userId).getOrThrow()

        return UserModel(
            userId = userId,
            nickName = userInfo.userName,
            characterId = userInfo.characterType,
            totalTime = userInfo.totalTime,
            totalDistance = userInfo.totalDistance,
        )
    }
}