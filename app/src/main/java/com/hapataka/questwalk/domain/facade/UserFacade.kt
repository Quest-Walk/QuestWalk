package com.hapataka.questwalk.domain.facade

import com.hapataka.questwalk.data.model.UserModel
import com.hapataka.questwalk.domain.usecase.CacheCurrentUserUserCase
import com.hapataka.questwalk.domain.usecase.GetCacheUserUseCase
import com.hapataka.questwalk.domain.usecase.GetUserIdFromPrefUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserFacade @Inject constructor(
    private val cacheCurrentUserUseCase: CacheCurrentUserUserCase,
    private val getCacheUserUseCase: GetCacheUserUseCase,
) {
    suspend fun getLoginUserToken(): UserModel? = withContext(Dispatchers.IO) {
        async { cacheCurrentUserUseCase() }.await()

        return@withContext getCacheUserUseCase()
    }
}