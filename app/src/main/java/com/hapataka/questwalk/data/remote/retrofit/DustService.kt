package com.hapataka.questwalk.data.remote.retrofit

import com.hapataka.questwalk.data.remote.dto.dust.Dust
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface DustService {
    @GET("getCtprvnRltmMesureDnsty")
    suspend fun getDust(
        @QueryMap queryMap: Map<String, String>
    ): Dust
}