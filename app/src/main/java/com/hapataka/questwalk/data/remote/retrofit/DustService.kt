package com.hapataka.questwalk.data.remote.retrofit

import com.hapataka.questwalk.data.remote.dto.dust.Dust
import com.hapataka.questwalk.data.remote.dto.station.StationResponse
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface DustService {
    @GET("ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty")
    suspend fun getDust(
        @QueryMap queryMap: Map<String, String>
    ): Dust

    @GET("MsrstnInfoInqireSvc/getNearbyMsrstnList")
    suspend fun getStation(
        @QueryMap queryMap: Map<String, String>
    ): StationResponse
}