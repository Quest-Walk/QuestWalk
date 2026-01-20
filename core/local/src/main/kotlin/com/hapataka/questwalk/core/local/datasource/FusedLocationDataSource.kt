package com.hapataka.questwalk.core.local.datasource

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.hapataka.questwalk.core.dataapi.datasource.LocationDataSource
import com.hapataka.questwalk.core.dataapi.model.LocationDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FusedLocationDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) : LocationDataSource {

    private val client by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): LocationDto? = withContext(Dispatchers.IO) {
        runCatching {
            val location = client.lastLocation.await()
            location?.let {
                LocationDto(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    accuracy = it.accuracy,
                    timestamp = it.time,
                )
            }
        }.getOrNull()
    }
}
