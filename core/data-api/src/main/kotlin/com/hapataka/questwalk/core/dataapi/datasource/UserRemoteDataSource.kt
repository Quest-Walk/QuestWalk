package com.hapataka.questwalk.core.dataapi.datasource

import com.hapataka.questwalk.core.dataapi.model.UserDto

interface UserRemoteDataSource {
    suspend fun getUserInfo(userId: String): Result<UserDto>
    suspend fun postUserInfo(userDto: UserDto): Result<Unit>
    suspend fun updateUserInfo(userDto: UserDto): Result<Unit>
}
