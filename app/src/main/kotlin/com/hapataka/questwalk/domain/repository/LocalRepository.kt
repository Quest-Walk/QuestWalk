package com.hapataka.questwalk.domain.repository

interface LocalRepository {
    fun setUserId(id: String)
    fun getUserId(): String

}