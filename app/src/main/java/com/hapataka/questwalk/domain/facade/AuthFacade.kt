package com.hapataka.questwalk.domain.facade

import com.hapataka.questwalk.domain.usecase.CacheCurrentUserUserCase
import com.hapataka.questwalk.domain.usecase.GetUserIdFromPrefUseCase
import com.hapataka.questwalk.domain.usecase.LoginByIdAndPwUseCase
import com.hapataka.questwalk.domain.usecase.RegisterByIdAndPwUseCase
import com.hapataka.questwalk.domain.usecase.SetUserIdToPrefUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthFacade @Inject constructor(
    private val setUserIdToPrefUseCase: SetUserIdToPrefUseCase,
    private val loginByIdAndPwUseCase: LoginByIdAndPwUseCase,
    private val cacheCurrentUserUserCase: CacheCurrentUserUserCase,
    private val getUserIdFromPrefUseCase: GetUserIdFromPrefUseCase,
    private val registerByIdAndPwUseCase: RegisterByIdAndPwUseCase
) {
    suspend fun loginByIdAndPw(id: String, password: String): Result<Boolean> {
        val result = loginByIdAndPwUseCase(id, password)

        if (result.isSuccess) {
            setUserIdToPrefUseCase(id)
            cacheCurrentUserUserCase()
        }
        return result
    }

    suspend fun registerByIdAndPw(id: String, password: String): Result<Boolean> {
        val result = registerByIdAndPwUseCase(id, password)

        return if (result.isSuccess) tryLogin(id, password) else result
    }

    private suspend fun tryLogin(id: String, password: String): Result<Boolean> {
        for (i in 1 .. 3) {
            val loginResult = loginByIdAndPwUseCase(id, password)

            if (loginResult.isSuccess) {
                setUserIdToPrefUseCase(id)
                cacheCurrentUserUserCase()
                return Result.success(true)
            }
            delay(1000)
        }
        return Result.failure(Exception("Login failed"))
    }

    suspend fun getUserIdFromPref(): Flow<String?> {
        return getUserIdFromPrefUseCase()
    }
}