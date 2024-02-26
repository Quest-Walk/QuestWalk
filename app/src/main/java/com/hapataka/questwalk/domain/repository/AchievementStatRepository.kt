package com.hapataka.questwalk.domain.repository

interface AchievementStatRepository {

    fun updateAchievementStatCount()
    fun loadAchievement(achievementId: Int)

}