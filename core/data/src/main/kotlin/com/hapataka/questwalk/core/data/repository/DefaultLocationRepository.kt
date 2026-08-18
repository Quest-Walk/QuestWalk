package com.hapataka.questwalk.core.data.repository

import android.location.Location as AndroidLocation
import com.hapataka.questwalk.core.dataapi.datasource.LocationDataSource
import com.hapataka.questwalk.core.dataapi.model.LocationDto
import com.hapataka.questwalk.core.domain.repository.LocationRepository
import com.hapataka.questwalk.core.model.Location
import com.hapataka.questwalk.core.model.LocationUpdate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class DefaultLocationRepository @Inject constructor(
    private val locationDataSource: LocationDataSource,
) : LocationRepository {

    private var prevLocationDto: LocationDto? = null

    override suspend fun getCurrentLocation(): Location? {
        return locationDataSource.getCurrentLocation()?.toLocation()
    }

    override fun getLocationUpdates(): Flow<LocationUpdate> {
        prevLocationDto = null

        return locationDataSource.getLocationUpdates()
            .mapNotNull { current ->
                val filtered = filterLocation(current)
                if (filtered != null) {
                    prevLocationDto = current
                }
                filtered
            }
    }

    private fun filterLocation(current: LocationDto): LocationUpdate? {
        val prev = prevLocationDto

        if (current.accuracy > MAX_ACCURACY) {
            return null
        }

        if (prev == null) {
            prevLocationDto = current
            return LocationUpdate(
                location = current.toLocation(),
                distance = 0f,
            )
        }

        val distance = calculateDistance(prev, current)

        if (distance < MIN_DISTANCE) {
            return null
        }

        if (distance > current.maxAllowedDistanceFrom(prev)) {
            return null
        }

        return LocationUpdate(
            location = current.toLocation(),
            distance = distance,
        )
    }

    private fun calculateDistance(from: LocationDto, to: LocationDto): Float {
        val results = FloatArray(1)
        AndroidLocation.distanceBetween(
            from.latitude,
            from.longitude,
            to.latitude,
            to.longitude,
            results,
        )
        return results[0]
    }

    private fun LocationDto.toLocation(): Location {
        return Location(
            latitude = latitude.toFloat(),
            longitude = longitude.toFloat(),
        )
    }

    private fun LocationDto.maxAllowedDistanceFrom(prev: LocationDto): Float {
        val elapsedSeconds = ((timestamp - prev.timestamp) / 1000f)
            .coerceAtLeast(1f)
        val accuracyTolerance = accuracy + prev.accuracy
        return (elapsedSeconds * MAX_REASONABLE_SPEED_MPS) + accuracyTolerance
    }

    companion object {
        // UX policy: keep plausible walking traces without sending location data to map-matching APIs.
        private const val MIN_DISTANCE = 10f
        private const val MAX_ACCURACY = 25f
        private const val MAX_REASONABLE_SPEED_MPS = 6f
    }
}
