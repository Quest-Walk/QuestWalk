package com.hapataka.questwalk.core.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_user")
data class UserEntity(
    @PrimaryKey val userId: String,
    @ColumnInfo("user_name") val userName: String,
    @ColumnInfo("character_type") val characterType: Int,
    @ColumnInfo("total_time") val totalTime: Long,
    @ColumnInfo("total_distance") val totalDistance: Float,
    @ColumnInfo("total_step") val totalStep: Long,
    @ColumnInfo("success_keywords") val successKeywords: List<String>,
    @ColumnInfo("achievement_ids") val achievementIds: List<Int>,
)
