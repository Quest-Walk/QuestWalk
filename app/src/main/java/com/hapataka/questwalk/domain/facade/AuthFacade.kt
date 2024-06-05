package com.hapataka.questwalk.domain.facade

import com.hapataka.questwalk.domain.usecase.CacheCurrentUserUserCase
import com.hapataka.questwalk.domain.usecase.GetUserIdFromPrefUseCase
import com.hapataka.questwalk.domain.usecase.LoginByIdAndPwUseCase
import com.hapataka.questwalk.domain.usecase.SetUserIdToPrefUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthFacade @Inject constructor(
    private val setUserIdToPrefUseCase: SetUserIdToPrefUseCase,
    private val loginByIdAndPwUseCase: LoginByIdAndPwUseCase,
    private val cacheCurrentUserUserCase: CacheCurrentUserUserCase,
    private val getUserIdFromPrefUseCase: GetUserIdFromPrefUseCase
) {
    suspend fun loginByIdAndPw(userId: String, password: String): Result<Boolean> {
        val result = loginByIdAndPwUseCase(userId, password)

        if (result.isSuccess) {
            setUserIdToPrefUseCase(userId)
            cacheCurrentUserUserCase()
        }
        return result
    }

    suspend fun getUserIdFromPref(): Flow<String?> {
        return getUserIdFromPrefUseCase()
    }
}