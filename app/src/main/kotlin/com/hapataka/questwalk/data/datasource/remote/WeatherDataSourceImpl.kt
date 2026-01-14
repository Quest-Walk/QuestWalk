package com.hapataka.questwalk.data.datasource.remote

import com.hapataka.questwalk.BuildConfig
import com.hapataka.questwalk.core.remote.api.WeatherDataSource
import com.hapataka.questwalk.core.remote.model.DustDto
import com.hapataka.questwalk.core.remote.model.WeatherDto
import com.hapataka.questwalk.data.dto.weather.Item
import com.hapataka.questwalk.domain.repository.LocationRepository
import com.hapataka.questwalk.ui.weather.LatXLngY
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class WeatherDataSourceImpl @Inject constructor(
    private val locationRepository: LocationRepository,
) : WeatherDataSource {

    private val weatherService = RetrofitClient.weatherApi
    private val dustService = RetrofitClient.dustApi

    override suspend fun getWeatherInfo(): List<WeatherDto> {
        val currentLocation = locationRepository.getCurrent().location
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

        val items = weatherService.getWeatherInfo(queries).response.body.items.item
        val weatherEntities = convertToWeatherDto(items)

        return weatherEntities
            .filter { it.fcstTime.toInt() >= requestTime || it.fcstDate.toInt() > requestDay }
            .take(10)
    }

    override suspend fun getDustInfo(): DustDto {
        val currentLocation = locationRepository.getCurrent()
        val besselLocation = convertToBesselLocation(currentLocation)

        val stationQueryMap = mapOf(
            "serviceKey" to BuildConfig.weather_key,
            "returnType" to "json",
            "tmX" to besselLocation.first.toString(),
            "tmY" to besselLocation.second.toString()
        )

        val stationResponse = dustService.getStation(stationQueryMap)
        val stationName = stationResponse.response.body.items.firstOrNull()?.stationName ?: ""

        val dustQueryMap = mapOf(
            "serviceKey" to BuildConfig.weather_key,
            "returnType" to "json",
            "stationName" to stationName,
            "dataTerm" to "DAILY",
            "ver" to "1.0"
        )

        val dustResponse = dustService.getDust(dustQueryMap)
        val dustItem = dustResponse.dustResponse.dustBody.dustItems.firstOrNull()

        return DustDto(
            pm10Value = dustItem?.pm10Value?.toIntOrNull() ?: -1,
            pm25Value = dustItem?.pm25Value?.toIntOrNull() ?: -1,
        )
    }

    private fun convertToWeatherDto(items: List<Item>): List<WeatherDto> {
        val itemsGroup = items.groupBy { "${it.fcstDate}${it.fcstTime}" }
        return itemsGroup.map { (_, item) ->
            val sky = item.firstOrNull { it.category == "SKY" }?.fcstValue ?: "0"
            val pty = item.firstOrNull { it.category == "PTY" }?.fcstValue ?: "0"
            val tmp = item.firstOrNull { it.category == "TMP" }?.fcstValue ?: "0"

            WeatherDto(
                fcstDate = item[0].fcstDate,
                fcstTime = item[0].fcstTime,
                sky = sky,
                precipType = pty,
                temp = tmp
            )
        }
    }

    private fun convertToXY(latX: Double, lngY: Double): LatXLngY {
        val RE = 6371.00877
        val GRID = 5.0
        val SLAT1 = 30.0
        val SLAT2 = 60.0
        val OLON = 126.0
        val OLAT = 38.0
        val XO = 43.0
        val YO = 136.0

        val DEGRAD = Math.PI / 180.0

        val re = RE / GRID
        val slat1 = SLAT1 * DEGRAD
        val slat2 = SLAT2 * DEGRAD
        val olon = OLON * DEGRAD
        val olat = OLAT * DEGRAD

        val sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5)
        val sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5).let { Math.pow(it, sn) * Math.cos(slat1) / sn }
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

    private fun convertToBesselLocation(location: com.hapataka.questwalk.domain.entity.LocationEntity): Pair<Double, Double> {
        val wgs84Proj = "+proj=longlat +ellps=bessel +no_defs"
        val wgs84System = org.locationtech.proj4j.CRSFactory().createFromParameters("WGS84", wgs84Proj)

        val besselProj =
            "+proj=tmerc +lat_0=38 +lon_0=127.0028902777778 +k=1 +x_0=200000 +y_0=500000 +ellps=bessel +units=m +no_defs +towgs84=-115.80,474.99,674.11,1.16,-2.31,-1.63,6.43"
        val besselSystem = org.locationtech.proj4j.CRSFactory().createFromParameters("Bessel", besselProj)

        val currentLocation = org.locationtech.proj4j.ProjCoordinate(
            location.location.second.toDouble(),
            location.location.first.toDouble()
        )
        val transformLocation = org.locationtech.proj4j.ProjCoordinate()

        val coordinateTransform = org.locationtech.proj4j.CoordinateTransformFactory()
            .createTransform(wgs84System, besselSystem)
        val projCoordinate = coordinateTransform.transform(currentLocation, transformLocation)

        return Pair(projCoordinate.x, projCoordinate.y)
    }

    private fun setRequestDateTime(): Pair<String, String> {
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
}
