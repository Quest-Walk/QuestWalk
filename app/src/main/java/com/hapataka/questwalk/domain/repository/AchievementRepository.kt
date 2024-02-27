package com.hapataka.questwalk.domain.repository

interface AchievementRepository {

    fun setAchievState(id: Int)
    fun getAchievStateById(id: Int)
    fun getAchievState()

}