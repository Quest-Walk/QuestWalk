package com.hapataka.questwalk.data.remote.repository

import android.util.Log
import com.example.weatherex.dto.Item
import com.hapataka.questwalk.data.remote.retrofit.WeatherService
import com.hapataka.questwalk.domain.entity.WeatherEntity
import com.hapataka.questwalk.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherService: WeatherService
) : WeatherRepository {

    override suspend fun getWeatherInfo(quries: Map<String, String>): MutableList<WeatherEntity> {
        Log.d("WeatherRepositoryImpl:","WeatherRepositoryImpl: $quries")
        val items = weatherService.getWeatherInfo(quries).response.body.items.item
        return convertToWeatherEntity(items)
    }

    private fun convertToWeatherEntity(items: List<Item>): MutableList<WeatherEntity> {
        val weatherEntityList = mutableListOf<WeatherEntity>()
        val itemsGroup = items.groupBy { "${it.fcstDate}${it.fcstTime}" }
        itemsGroup.forEach { (dateTime, items) ->
            var sky = ""
            var precipType = ""
            var temp = ""
            items.forEach {
                when (it.category) {
                    "SKY" -> { sky = it.fcstValue }
                    "PTY" -> { precipType = it.fcstValue }
                    "TMP" -> { temp = it.fcstValue }
                }
            }
            weatherEntityList.add(
                WeatherEntity(
                    fcstDate = items[0].fcstDate,
                    fcstTime = items[0].fcstTime,
                    baseDate =  items[0].baseDate,
                    sky = sky,
                    precipType = precipType,
                    temp = temp
                )
            )
        }
        return weatherEntityList
    }

}

// PTY 강수 형태 (초단기) 없음(0), 비(1), 비/눈(2), 눈(3), 빗방울(5), 빗방울눈날림(6), 눈날림(7)
//             (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
// SKY 하늘 상태 0~5 맑음 6~8 구름 많음 9~10 흐림
// T1H 기온