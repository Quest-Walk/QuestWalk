package com.hapataka.questwalk.core.remote.api

import com.hapataka.questwalk.core.remote.model.dust.DustApiResponse
import com.hapataka.questwalk.core.remote.model.dust.StationApiResponse
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface DustApi {
    @GET("ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty")
    suspend fun getDust(
        @QueryMap queries: Map<String, String>,
    ): DustApiResponse

    @GET("MsrstnInfoInqireSvc/getNearbyMsrstnList")
    suspend fun getStation(
        @QueryMap queries: Map<String, String>,
    ): StationApiResponse
}
