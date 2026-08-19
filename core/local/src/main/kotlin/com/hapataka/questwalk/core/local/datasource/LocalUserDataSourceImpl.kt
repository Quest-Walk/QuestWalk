package com.hapataka.questwalk.core.local.datasource

import com.hapataka.questwalk.core.local.UserDao
import com.hapataka.questwalk.core.local.api.LocalUserDataSource
import com.hapataka.questwalk.core.local.toEntity
import com.hapataka.questwalk.core.local.toModel
import com.hapataka.questwalk.core.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalUserDataSourceImpl @Inject constructor(
    private val userDao: UserDao
) : LocalUserDataSource {
    override fun getCurrentUser(): Flow<User?> {
        return userDao.getCurrentUser().map { it?.toModel() }
    }

    override suspend fun existLoginUser() = userDao.existLoginUser()

    override suspend fun insertUser(user: User) {
        userDao.insertUser(user.toEntity())
    }

    override suspend fun updateUser(user: User) {
        userDao.updateUser(user.toEntity())
    }

    override suspend fun clearUsers() {
        userDao.clearUsers()
    }
}