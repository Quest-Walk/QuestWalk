package com.hapataka.questwalk.data.repository

import android.util.Log
import com.hapataka.questwalk.data.datasource.remote.RetrofitClient
import com.hapataka.questwalk.data.dto.WeatherItem
import com.hapataka.questwalk.domain.entity.WeatherEntity
import com.hapataka.questwalk.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor () : WeatherRepository {
    private val weatherService = RetrofitClient.weatherApi

    override suspend fun getWeatherInfo(quries: Map<String, String>): MutableList<WeatherEntity> {
        Log.d("WeatherRepositoryImpl:","WeatherRepositoryImpl: $quries")
        val items = weatherService.getWeatherInfo(quries).weather.weatherBody.weatherItems.weatherItem
        return convertToWeatherEntity(items)
    }

    private fun convertToWeatherEntity(weatherItems: List<WeatherItem>): MutableList<WeatherEntity> {
        val itemsGroup = weatherItems.groupBy { "${it.fcstDate}${it.fcstTime}" }
        val result =  itemsGroup.map { (dateTime, item) ->
            val sky = item.first { it.category == "SKY" }.fcstValue
            val pty = item.first { it.category == "PTY" }.fcstValue
            val tmp = item.first { it.category == "TMP" }.fcstValue

            WeatherEntity(
                fcstDate = item[0].fcstDate,
                fcstTime = item[0].fcstTime,
                baseDate = item[0].baseDate,
                sky = sky,
                precipType = pty,
                temp = tmp
            )
        }.toMutableList()
        Log.d("WeatherRepository:","$result")
        return result
    }
}

// PTY 강수 형태 (초단기) 없음(0), 비(1), 비/눈(2), 눈(3), 빗방울(5), 빗방울눈날림(6), 눈날림(7)
//             (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
// SKY 하늘 상태 0~5 맑음 6~8 구름 많음 9~10 흐림
// T1H 기온