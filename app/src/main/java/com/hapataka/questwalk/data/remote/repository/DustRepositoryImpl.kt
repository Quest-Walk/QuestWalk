package com.hapataka.questwalk.data.remote.repository

import android.util.Log
import com.hapataka.questwalk.data.remote.dto.dust.Dust
import com.hapataka.questwalk.data.remote.dto.station.StationResponse
import com.hapataka.questwalk.data.remote.dto.tm.TmResponse
import com.hapataka.questwalk.data.remote.retrofit.RetrofitClient
import com.hapataka.questwalk.domain.entity.DustEntity
import com.hapataka.questwalk.domain.entity.StationEntity
import com.hapataka.questwalk.domain.entity.TmEntity
import com.hapataka.questwalk.domain.repository.DustRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DustRepositoryImpl: DustRepository {
    private val dustService = RetrofitClient.dustApi

    override suspend fun getDustInfo(queryMap: Map<String, String>) = withContext(Dispatchers.IO) {
        convertToDustEntity(dustService.getDust(queryMap))
    }

    override suspend fun getStation(queryMap: Map<String, String>) = withContext(Dispatchers.IO) {
        convertToStationEntity(dustService.getStation(queryMap))
    }

    override suspend fun getTmLocation(queryMap: Map<String, String>) = withContext(Dispatchers.IO) {
        convertToTmEntity(dustService.getTmLocation(queryMap))
    }

    private fun convertToDustEntity(item: Dust): DustEntity {
        return DustEntity(
            pm10Value = item.dustResponse.dustBody.dustItems[0].pm10Value.toInt(),
            pm25Value = item.dustResponse.dustBody.dustItems[0].pm25Value.toInt()
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

    private fun convertToTmEntity(item: TmResponse): TmEntity {
        Log.d("StationRepositoryImpl:","$item")
        return TmEntity(
            tmx = item.response.body.items[0].tmX,
            tmy = item.response.body.items[0].tmY
        )
    }
}