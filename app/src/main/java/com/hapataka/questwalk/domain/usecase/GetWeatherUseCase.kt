package com.hapataka.questwalk.domain.usecase

import com.hapataka.questwalk.BuildConfig
import com.hapataka.questwalk.domain.entity.WeatherEntity
import com.hapataka.questwalk.domain.repository.LocationRepository
import com.hapataka.questwalk.domain.repository.WeatherRepository
import com.hapataka.questwalk.ui.weather.LatXLngY
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationRepo: LocationRepository
) {
    suspend operator fun invoke(): List<WeatherEntity> {
        val currentLocation = locationRepo.getCurrent().location
        val convertXy = convertToXY(currentLocation.first.toDouble(), currentLocation.second.toDouble())
        val requestTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH00")).toInt()
        val requestDay = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt()
        val requestDateTime = setRequestDateTime()
        val queries = mapOf(
            "serviceKey" to BuildConfig.weather_key,
            "dataType" to "json",
            "base_date" to requestDateTime.first,
            "base_time" to requestDateTime.second,
            "numOfRows" to "144",
            "nx" to convertXy.x.toInt().toString(),
            "ny" to convertXy.y.toInt().toString(),
        )
        val weatherInfo = weatherRepository.getWeatherInfo(queries)
        return weatherInfo.filter { it.fcstTime.toInt() >= requestTime || it.fcstDate.toInt() > requestDay }
            .take(10)
    }

    private fun convertToXY(latX: Double, lngY: Double): LatXLngY {
        val RE = 6371.00877 // 지구 반경(km)
        val GRID = 5.0 // 격자 간격(km)
        val SLAT1 = 30.0 // 투영 위도1(degree)
        val SLAT2 = 60.0 // 투영 위도2(degree)
        val OLON = 126.0 // 기준점 경도(degree)
        val OLAT = 38.0 // 기준점 위도(degree)
        val XO = 43.0 // 기준점 X좌표(GRID)
        val YO = 136.0 // 기준점 Y좌표(GRID)

        val DEGRAD = Math.PI / 180.0
        val RADDEG = 180.0 / Math.PI

        val re = RE / GRID
        val slat1 = SLAT1 * DEGRAD
        val slat2 = SLAT2 * DEGRAD
        val olon = OLON * DEGRAD
        val olat = OLAT * DEGRAD

        val sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5)
        val sf =
            Math.tan(Math.PI * 0.25 + slat1 * 0.5).let { Math.pow(it, sn) * Math.cos(slat1) / sn }
        val ro = Math.tan(Math.PI * 0.25 + olat * 0.5).let { re * sf / Math.pow(it, sn) }

        val rs = LatXLngY()

        rs.lat = latX
        rs.lng = lngY

        val ra = Math.tan(Math.PI * 0.25 + (latX) * DEGRAD * 0.5).let { re * sf / Math.pow(it, sn) }
        var theta = lngY * DEGRAD - olon

        if (theta > Math.PI) theta -= 2.0 * Math.PI
        if (theta < -Math.PI) theta += 2.0 * Math.PI

        theta *= sn

        rs.x = (ra * Math.sin(theta) + XO + 0.5).toInt().toDouble()
        rs.y = (ro - ra * Math.cos(theta) + YO + 0.5).toInt().toDouble()

        return rs
    }

    private fun setRequestDateTime(): Pair<String,String> {
        val today = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE)
        val yesterday = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE)
        val requestTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmm"))
        val result = when {
            requestTime < "0300"  -> Pair(yesterday, "2300")
            requestTime < "0600" -> Pair(today, "0200")
            requestTime < "0900" -> Pair(today, "0500")
            requestTime < "1200" -> Pair(today, "0800")
            requestTime < "1500" -> Pair(today, "1100")
            requestTime < "1800" -> Pair(today, "1400")
            requestTime < "2100" -> Pair(today, "1700")
            requestTime < "2359" -> Pair(today, "2000")
            else -> Pair(yesterday, "2300")
        }
        return result
    }
}