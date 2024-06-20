package com.hapataka.questwalk.domain.facade

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

    suspend fun getCacheUser(): UserModel? {
        return getCacheUserUseCase()
    }
}