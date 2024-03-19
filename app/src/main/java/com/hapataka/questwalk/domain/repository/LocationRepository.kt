package com.hapataka.questwalk.domain.repository

import android.location.Address
import com.hapataka.questwalk.domain.entity.LocationEntity

interface LocationRepository {
    fun startRequest(callback: (LocationEntity) -> Unit)
    fun finishRequest ()
    suspend fun getCurrent(): LocationEntity
    suspend fun getAddress(): Address?
}