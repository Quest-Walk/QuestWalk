package com.hapataka.questwalk.domain.repository

import com.hapataka.questwalk.data.remote.dto.dust.Dust
import retrofit2.http.QueryMap

interface DustRepository {
    suspend fun getDust(@QueryMap queryMap: Map<String, String>): Dust
}