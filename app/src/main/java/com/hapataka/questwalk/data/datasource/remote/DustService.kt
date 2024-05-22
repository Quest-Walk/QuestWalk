package com.hapataka.questwalk.data.datasource.remote

import com.hapataka.questwalk.data.dto.DustResponse
import com.hapataka.questwalk.data.dto.StationResponse
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface DustService {
    @GET("ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty")
    suspend fun getDust(
        @QueryMap queryMap: Map<String, String>
    ): DustResponse

    @GET("MsrstnInfoInqireSvc/getNearbyMsrstnList")
    suspend fun getStation(
        @QueryMap queryMap: Map<String, String>
    ): StationResponse
}