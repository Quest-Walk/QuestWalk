package com.hapataka.questwalk.core.remote.datasource

import com.hapataka.questwalk.core.dataapi.datasource.DustRemoteDataSource
import com.hapataka.questwalk.core.dataapi.model.DustDto
import com.hapataka.questwalk.core.dataapi.model.LocationDto
import com.hapataka.questwalk.core.remote.api.DustApi
import com.hapataka.questwalk.core.remote.util.CoordinateConverter
import javax.inject.Inject
import javax.inject.Named

class HttpDustDataSource @Inject constructor(
    private val dustApi: DustApi,
    @Named("weatherApiKey") private val apiKey: String,
) : DustRemoteDataSource {

    override suspend fun getDust(location: LocationDto): Result<DustDto> = runCatching {
        // 1. 위경도를 TM 좌표로 변환
        val (tmX, tmY) = CoordinateConverter.toBesselTM(location.latitude, location.longitude)

        // 2. 가장 가까운 측정소 조회
        val stationQueries = mapOf(
            "serviceKey" to apiKey,
            "returnType" to "json",
            "tmX" to tmX.toString(),
            "tmY" to tmY.toString(),
        )

        val stationResponse = dustApi.getStation(stationQueries)
        val station = stationResponse.response.body.items.firstOrNull()
            ?: throw Exception("측정소를 찾을 수 없습니다")

        // 3. 측정소의 미세먼지 정보 조회
        val dustQueries = mapOf(
            "serviceKey" to apiKey,
            "returnType" to "json",
            "stationName" to station.stationName,
            "dataTerm" to "DAILY",
            "ver" to "1.0",
        )

        val dustResponse = dustApi.getDust(dustQueries)
        val dustItem = dustResponse.response.body.items.firstOrNull()

        DustDto(
            pm10Value = dustItem?.pm10Value?.toIntOrNull() ?: -1,
            pm25Value = dustItem?.pm25Value?.toIntOrNull() ?: -1,
            stationName = station.stationName,
            stationAddress = station.addr,
        )
    }
}
