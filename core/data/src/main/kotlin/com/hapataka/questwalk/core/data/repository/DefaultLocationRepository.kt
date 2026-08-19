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
    private val locationKalmanFilter = LocationKalmanFilter()

    override suspend fun getCurrentLocation(): Location? {
        return locationDataSource.getCurrentLocation()?.toLocation()
    }

    override fun getLocationUpdates(): Flow<LocationUpdate> {
        prevLocationDto = null
        locationKalmanFilter.reset()

        return locationDataSource.getLocationUpdates()
            .mapNotNull { current ->
                filterLocation(current)
            }
    }

    private fun filterLocation(current: LocationDto): LocationUpdate? {
        val prev = prevLocationDto

        if (current.accuracy > MAX_ACCURACY) {
            return null
        }

        if (prev != null) {
            val rawDistance = calculateDistance(prev, current)
            if (rawDistance > current.maxAllowedDistanceFrom(prev)) {
                return null
            }
        }

        val smoothed = locationKalmanFilter.process(current)

        if (prev == null) {
            prevLocationDto = smoothed
            return LocationUpdate(
                location = smoothed.toLocation(),
                distance = 0f,
            )
        }

        val distance = calculateDistance(prev, smoothed)

        if (distance < MIN_DISTANCE) {
            return null
        }

        prevLocationDto = smoothed
        return LocationUpdate(
            location = smoothed.toLocation(),
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
        private const val MIN_DISTANCE = 5f
        private const val MAX_ACCURACY = 25f
        private const val MAX_REASONABLE_SPEED_MPS = 6f
    }
}

private class LocationKalmanFilter {
    private var isInitialized = false
    private var latitude = 0.0
    private var longitude = 0.0
    private var varianceMeters = 0.0
    private var timestamp = 0L

    fun reset() {
        isInitialized = false
        latitude = 0.0
        longitude = 0.0
        varianceMeters = 0.0
        timestamp = 0L
    }

    fun process(location: LocationDto): LocationDto {
        val accuracy = location.accuracy
            .coerceAtLeast(MIN_ACCURACY_METERS)
            .toDouble()

        if (!isInitialized) {
            isInitialized = true
            latitude = location.latitude
            longitude = location.longitude
            varianceMeters = accuracy * accuracy
            timestamp = location.timestamp
            return location
        }

        val elapsedSeconds = ((location.timestamp - timestamp) / 1000.0)
            .coerceAtLeast(0.0)
        if (elapsedSeconds > 0.0) {
            varianceMeters += elapsedSeconds * PROCESS_NOISE_METERS_PER_SECOND
        }

        val measurementVariance = accuracy * accuracy
        val kalmanGain = varianceMeters / (varianceMeters + measurementVariance)
        latitude += kalmanGain * (location.latitude - latitude)
        longitude += kalmanGain * (location.longitude - longitude)
        varianceMeters *= 1.0 - kalmanGain
        timestamp = location.timestamp

        return location.copy(
            latitude = latitude,
            longitude = longitude,
        )
    }

    private companion object {
        private const val MIN_ACCURACY_METERS = 3f
        private const val PROCESS_NOISE_METERS_PER_SECOND = 4.0
    }
}
