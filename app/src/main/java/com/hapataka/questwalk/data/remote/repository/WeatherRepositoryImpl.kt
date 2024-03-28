package com.hapataka.questwalk.data.remote.repository


import com.hapataka.questwalk.data.remote.dto.weather.Item
import com.hapataka.questwalk.data.remote.retrofit.RetrofitClient
import com.hapataka.questwalk.domain.entity.WeatherEntity
import com.hapataka.questwalk.domain.repository.WeatherRepository

class WeatherRepositoryImpl (
) : WeatherRepository {
    private val weatherService = RetrofitClient.weatherApi

    override suspend fun getWeatherInfo(quries: Map<String, String>): MutableList<WeatherEntity> {
        val items = weatherService.getWeatherInfo(quries).response.body.items.item
        return convertToWeatherEntity(items)
    }

    private fun convertToWeatherEntity(items: List<Item>): MutableList<WeatherEntity> {
        val itemsGroup = items.groupBy { "${it.fcstDate}${it.fcstTime}" }
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
        return result
    }
}