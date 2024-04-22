package com.hapataka.questwalk.data.remote.repository

import com.hapataka.questwalk.data.remote.dto.dust.Dust
import com.hapataka.questwalk.data.remote.dto.station.StationResponse
import com.hapataka.questwalk.data.remote.retrofit.RetrofitClient
import com.hapataka.questwalk.domain.entity.DustEntity
import com.hapataka.questwalk.domain.entity.StationEntity
import com.hapataka.questwalk.domain.repository.DustRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DustRepositoryImpl @Inject constructor(): DustRepository {
    private val dustService = RetrofitClient.dustApi

    override suspend fun getDustInfo(queryMap: Map<String, String>) = withContext(Dispatchers.IO) {
        convertToDustEntity(dustService.getDust(queryMap))
    }

    override suspend fun getStation(queryMap: Map<String, String>) = withContext(Dispatchers.IO) {
        convertToStationEntity(dustService.getStation(queryMap))
    }

    private fun convertToDustEntity(item: Dust): DustEntity {
        val pm10Value = item.dustResponse.dustBody.dustItems[0].pm10Value
        val pm25Value = item.dustResponse.dustBody.dustItems[0].pm25Value

        return DustEntity(
            pm10Value = if (pm10Value == "-") -1 else pm10Value.toInt(),
            pm25Value = if (pm25Value == "-") -1 else pm25Value.toInt()
        )
    }

    private fun convertToStationEntity(item: StationResponse): StationEntity {
        val nearStation = item.response.body.items.minBy { it.tm }
        return StationEntity(
            stationName = nearStation.stationName,
            stationAddr = nearStation.addr,
            distance = nearStation.tm
        )
    }
}