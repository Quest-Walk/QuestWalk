package com.hapataka.questwalk.domain.repository

import com.hapataka.questwalk.domain.entity.LocationEntity

interface LocationRepository {
    fun startRequest(callback: (LocationEntity) -> Unit)
    fun finishRequest ()

    suspend fun getCurrent(): Pair<Float, Float>
}