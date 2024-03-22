package com.hapataka.questwalk.domain.repository

import com.hapataka.questwalk.data.remote.dto.dust.Dust
import com.hapataka.questwalk.domain.entity.DustEntity
import com.hapataka.questwalk.domain.entity.StationEntity
import com.hapataka.questwalk.domain.entity.TmEntity
import retrofit2.http.QueryMap

interface DustRepository {
    suspend fun getDustInfo(queryMap: Map<String, String>): DustEntity
    suspend fun getStation(queryMap: Map<String, String>): StationEntity
    suspend fun getTmLocation(queryMap: Map<String, String>): TmEntity
}