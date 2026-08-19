package com.hapataka.questwalk.core.remote.datasource

import com.hapataka.questwalk.core.dataapi.datasource.WeatherRemoteDataSource
import com.hapataka.questwalk.core.dataapi.model.ForecastDto
import com.hapataka.questwalk.core.dataapi.model.LocationDto
import com.hapataka.questwalk.core.dataapi.model.WeatherDto
import com.hapataka.questwalk.core.remote.api.WeatherApi
import com.hapataka.questwalk.core.remote.util.CoordinateConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Named

class HttpWeatherDataSource @Inject constructor(
    private val weatherApi: WeatherApi,
    @Named("weatherApiKey") private val apiKey: String,
) : WeatherRemoteDataSource {

    override suspend fun getWeather(location: LocationDto): Result<WeatherDto> = runCatching {
        val (gridX, gridY) = CoordinateConverter.toGridXY(location.latitude, location.longitude)
        val (baseDate, baseTime) = getRequestDateTime()

        val queries = mapOf(
            "serviceKey" to apiKey,
            "dataType" to "json",
            "base_date" to baseDate,
            "base_time" to baseTime,
            "numOfRows" to "144",
            "nx" to gridX.toString(),
            "ny" to gridY.toString(),
        )

        val response = weatherApi.getWeatherForecast(queries)
        val items = response.response.body.items.item

        val currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH00")).toInt()
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt()

        val groupedItems = items.groupBy { "${it.fcstDate}${it.fcstTime}" }

        val forecasts = groupedItems.entries
            .filter { (key, _) ->
                val fcstDate = key.substring(0, 8).toInt()
                val fcstTime = key.substring(8).toInt()
                fcstDate > currentDate || (fcstDate == currentDate && fcstTime >= currentTime)
            }
            .sortedBy { it.key }
            .take(FORECAST_HOURS)
            .map { (_, groupItems) ->
                val sky = groupItems.firstOrNull { it.category == "SKY" }?.fcstValue ?: "1"
                val pty = groupItems.firstOrNull { it.category == "PTY" }?.fcstValue ?: "0"
                val tmp = groupItems.firstOrNull { it.category == "TMP" }?.fcstValue ?: "0"
                val firstItem = groupItems.firstOrNull()

                ForecastDto(
                    fcstDate = firstItem?.fcstDate ?: baseDate,
                    fcstTime = firstItem?.fcstTime ?: baseTime,
                    sky = sky,
                    precipType = pty,
                    temp = tmp,
                )
            }

        WeatherDto(
            baseDate = baseDate,
            baseTime = baseTime,
            forecasts = forecasts,
        )
    }

    private fun getRequestDateTime(): Pair<String, String> {
        val today = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE)
        val yesterday = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE)
        val requestTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmm"))

        return when {
            requestTime < "0300" -> Pair(yesterday, "2300")
            requestTime < "0600" -> Pair(today, "0200")
            requestTime < "0900" -> Pair(today, "0500")
            requestTime < "1200" -> Pair(today, "0800")
            requestTime < "1500" -> Pair(today, "1100")
            requestTime < "1800" -> Pair(today, "1400")
            requestTime < "2100" -> Pair(today, "1700")
            requestTime < "2359" -> Pair(today, "2000")
            else -> Pair(yesterday, "2300")
        }
    }

    companion object {
        private const val FORECAST_HOURS = 12
    }
}
