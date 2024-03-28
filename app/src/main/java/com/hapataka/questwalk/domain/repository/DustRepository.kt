package com.hapataka.questwalk.domain.repository

import com.hapataka.questwalk.domain.entity.DustEntity
import com.hapataka.questwalk.domain.entity.StationEntity

interface DustRepository {
    suspend fun getDustInfo(queryMap: Map<String, String>): DustEntity
    suspend fun getStation(queryMap: Map<String, String>): StationEntity
}