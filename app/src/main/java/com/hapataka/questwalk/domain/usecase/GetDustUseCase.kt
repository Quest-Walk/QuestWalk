package com.hapataka.questwalk.domain.usecase

import android.location.Location
import com.hapataka.questwalk.BuildConfig
import com.hapataka.questwalk.domain.entity.DustEntity
import com.hapataka.questwalk.domain.entity.LocationEntity
import com.hapataka.questwalk.domain.repository.DustRepository
import com.hapataka.questwalk.domain.repository.LocationRepository
import org.locationtech.proj4j.CRSFactory
import org.locationtech.proj4j.CoordinateTransformFactory
import org.locationtech.proj4j.ProjCoordinate
import javax.inject.Inject

class GetDustUseCase @Inject constructor(
    private val locationRepo: LocationRepository,
    private val dustRepo: DustRepository
) {
    suspend operator fun invoke(): DustEntity {
        val currentLocation = locationRepo.getCurrent()
        val besselLocation = convertToBesselLocation(currentLocation)
        val stationQueryMap = requestStationQueryMap(besselLocation)
        val stationName = dustRepo.getStation(stationQueryMap).stationName

        val dustQueryMap = requestDustQueryMap(stationName)
        return dustRepo.getDustInfo(dustQueryMap)
    }

    private fun convertToBesselLocation(location: LocationEntity): Location {
        val wgs84Proj = "+proj=longlat +ellps=bessel +no_defs"
        val wgs84System = CRSFactory().createFromParameters("WGS84", wgs84Proj)

        val besselProj = "+proj=tmerc +lat_0=38 +lon_0=127.0028902777778 +k=1 +x_0=200000 +y_0=500000 +ellps=bessel +units=m +no_defs +towgs84=-115.80,474.99,674.11,1.16,-2.31,-1.63,6.43"
        val besselSystem = CRSFactory().createFromParameters("Bessel", besselProj)

        val currentLocation = ProjCoordinate(location.location.second.toDouble(), location.location.first.toDouble())
        val transformLocation = ProjCoordinate()

        val coordinateTransform = CoordinateTransformFactory().createTransform(wgs84System, besselSystem)
        val projCoordinate = coordinateTransform.transform(currentLocation, transformLocation)

        val resultLocation = Location("Bessel")
        resultLocation.latitude = projCoordinate.x
        resultLocation.longitude = projCoordinate.y
        return resultLocation
    }

    private fun requestStationQueryMap(besselLocation: Location): Map<String, String> {
        return mapOf(
            "serviceKey" to BuildConfig.weather_key,
            "returnType" to "json",
            "tmX" to besselLocation.latitude.toString(),
            "tmY" to besselLocation.longitude.toString()
        )
    }

    private fun requestDustQueryMap(station: String): Map<String, String> {
        return mapOf(
            "serviceKey" to BuildConfig.weather_key,
            "returnType" to "json",
            "stationName" to station,
            "dataTerm" to "DAILY",
            "ver" to "1.0"
        )
    }
}