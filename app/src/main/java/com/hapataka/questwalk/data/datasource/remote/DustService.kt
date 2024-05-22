package com.hapataka.questwalk.data.datasource.remote

import retrofit2.http.GET
import retrofit2.http.QueryMap

interface DustService {
    @GET("ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty")
    suspend fun getDust(
        @QueryMap queryMap: Map<String, String>
    ): com.hapataka.questwalk.data.dto.dust.Dust

    @GET("MsrstnInfoInqireSvc/getNearbyMsrstnList")
    suspend fun getStation(
        @QueryMap queryMap: Map<String, String>
    ): com.hapataka.questwalk.data.dto.station.StationResponse
}