package com.hapataka.questwalk.data.datasource.remote

import android.location.Location
import com.hapataka.questwalk.BuildConfig
import com.hapataka.questwalk.data.dto.DustDTO
import com.hapataka.questwalk.data.retrofit.DustService
import com.hapataka.questwalk.domain.repository.DustRemoteDataSource
import org.locationtech.proj4j.CRSFactory
import org.locationtech.proj4j.CoordinateTransformFactory
import org.locationtech.proj4j.ProjCoordinate
import javax.inject.Inject

class DustRemoteDataSourceImpl @Inject constructor(
    private val dustService: DustService
): DustRemoteDataSource {
    override suspend fun getDustInfo(currentLocation: Pair<Float, Float>): DustDTO? {
        val besselLocation = convertToBesselLocation(currentLocation)
        val stationQueryMap =  mapOf(
            "serviceKey" to BuildConfig.weather_key,
            "returnType" to "json",
            "tmX" to besselLocation.latitude.toString(),
            "tmY" to besselLocation.longitude.toString()
        )
        val stationList = kotlin.runCatching {
            dustService.getStation(stationQueryMap).station.stationBody.stationItems.sortedBy { it.tm }
        }.getOrNull()

        if (stationList != null) {
            for (station in stationList) {
                val dustQueryMap = mapOf(
                    "serviceKey" to BuildConfig.weather_key,
                    "returnType" to "json",
                    "stationName" to station.stationName,
                    "dataTerm" to "DAILY",
                    "ver" to "1.0"
                )
                val dustInfo = kotlin.runCatching {
                    dustService.getDust(dustQueryMap).dust.dustBody.dustDTO
                }.getOrNull()

                if (!dustInfo.isNullOrEmpty()) {
                    return dustInfo[0]
                }
            }
        }
        return null
    }

    private fun convertToBesselLocation(location: Pair<Float, Float>): Location {
        val wgs84Proj = "+proj=longlat +ellps=bessel +no_defs"
        val wgs84System = CRSFactory().createFromParameters("WGS84", wgs84Proj)

        val besselProj = "+proj=tmerc +lat_0=38 +lon_0=127.0028902777778 +k=1 +x_0=200000 +y_0=500000 +ellps=bessel +units=m +no_defs +towgs84=-115.80,474.99,674.11,1.16,-2.31,-1.63,6.43"
        val besselSystem = CRSFactory().createFromParameters("Bessel", besselProj)

        val currentLocation = ProjCoordinate(location.second.toDouble(), location.first.toDouble())
        val transformLocation = ProjCoordinate()

        val coordinateTransform = CoordinateTransformFactory().createTransform(wgs84System, besselSystem)
        val projCoordinate = coordinateTransform.transform(currentLocation, transformLocation)

        val resultLocation = Location("Bessel")
        resultLocation.latitude = projCoordinate.x
        resultLocation.longitude = projCoordinate.y
        return resultLocation
    }
}
