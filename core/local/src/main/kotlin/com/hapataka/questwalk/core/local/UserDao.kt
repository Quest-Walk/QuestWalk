package com.hapataka.questwalk.core.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM current_user")
    fun getCurrentUser(): Flow<UserEntity>

    @Query("SELECT EXISTS(SELECT * FROM current_user)")
    fun existLoginUser(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM current_user")
    suspend fun clearUsers()
}