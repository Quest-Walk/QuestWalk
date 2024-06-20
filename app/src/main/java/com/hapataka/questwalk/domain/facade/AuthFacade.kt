package com.hapataka.questwalk.domain.facade

import com.hapataka.questwalk.domain.usecase.CacheCurrentUserHistoriesUseCase
import com.hapataka.questwalk.domain.usecase.CacheCurrentUserUserCase
import com.hapataka.questwalk.domain.usecase.ClearUserCacheUseCase
import com.hapataka.questwalk.domain.usecase.DeleteUserInfoByIdUseCase
import com.hapataka.questwalk.domain.usecase.DropOutCurrentUserUseCase
import com.hapataka.questwalk.domain.usecase.GetCacheUserUseCase
import com.hapataka.questwalk.domain.usecase.GetUserIdFromPrefUseCase
import com.hapataka.questwalk.domain.usecase.LoginByIdAndPwUseCase
import com.hapataka.questwalk.domain.usecase.LogoutUseCase
import com.hapataka.questwalk.domain.usecase.ReauthCurrentUserUseCase
import com.hapataka.questwalk.domain.usecase.RegisterByIdAndPwUseCase
import com.hapataka.questwalk.domain.usecase.SetUserIdToPrefUseCase
import com.hapataka.questwalk.domain.usecase.UploadUserUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

const val LOGOUT_SUCCESS = 0

class AuthFacade @Inject constructor(
    private val setUserIdToPrefUseCase: SetUserIdToPrefUseCase,
    private val loginByIdAndPwUseCase: LoginByIdAndPwUseCase,
    private val cacheCurrentUserUserCase: CacheCurrentUserUserCase,
    private val getUserIdFromPrefUseCase: GetUserIdFromPrefUseCase,
    private val registerByIdAndPwUseCase: RegisterByIdAndPwUseCase,
    private val getCacheUserUseCase: GetCacheUserUseCase,
    private val uploadUserUseCase: UploadUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val clearUserCacheUseCase: ClearUserCacheUseCase,
    private val cacheCurrentUserHistoriesUseCase: CacheCurrentUserHistoriesUseCase,
    private val reauthCurrentUserUseCase: ReauthCurrentUserUseCase,
    private val dropOutCurrentUserUseCase: DropOutCurrentUserUseCase,
    private val deleteUserInfoByIdUseCase: DeleteUserInfoByIdUseCase
) {
    suspend fun loginByIdAndPw(id: String, password: String): Result<Boolean> {
        val result = loginByIdAndPwUseCase(id, password)

        if (result.isSuccess) {
            setUserIdToPrefUseCase(id)
            cacheCurrentUserUserCase()
            cacheCurrentUserHistoriesUseCase()
        }
        return result
    }

    suspend fun registerByIdAndPw(id: String, password: String): Result<Boolean> {
        val result = registerByIdAndPwUseCase(id, password)

        return if (result.isSuccess) tryLogin(id, password) else result
    }

    private suspend fun tryLogin(id: String, password: String): Result<Boolean> {
        for (i in 1..3) {
            val loginResult = loginByIdAndPwUseCase(id, password)

            if (loginResult.isSuccess) {
                setUserIdToPrefUseCase(id)
                cacheCurrentUserUserCase()
                cacheCurrentUserHistoriesUseCase
                val user = getCacheUserUseCase()

                if (user != null) {
                    uploadUserUseCase(user)
                }
                return Result.success(true)
            }
            delay(1000)
        }
        return Result.failure(Exception("Login failed"))
    }

    suspend fun getUserIdFromPref(): Flow<String?> {
        return getUserIdFromPrefUseCase()
    }

    suspend fun logout(): Result<Int> {
        val result = logoutUseCase()

        if (result.isSuccess) {
            clearUserCacheUseCase()
            return Result.success(LOGOUT_SUCCESS)
        } else {
            return Result.failure(result.exceptionOrNull() ?: Exception())
        }
    }

    suspend fun reauthCurrentUser(pw: String): Result<Unit> {
        return reauthCurrentUserUseCase(pw)
    }

    suspend fun dropOutCurrentUser(): Result<Unit> {
        return kotlin.runCatching {
            val userId = getCacheUserUseCase()?.userId ?: return@runCatching
            val deleteResult = deleteUserInfoByIdUseCase(userId)

            clearUserCacheUseCase()

            if (deleteResult.isSuccess) {
                dropOutCurrentUserUseCase()
            } else {
                repeat(3) {
                    dropOutCurrentUserUseCase()
                    delay(3000)
                }
            }
        }

    }
}