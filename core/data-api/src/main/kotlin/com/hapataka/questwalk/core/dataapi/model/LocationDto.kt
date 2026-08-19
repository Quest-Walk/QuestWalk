package com.hapataka.questwalk.core.dataapi.model

data class LocationDto(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float = 0f,
    val speed: Float = 0f,
    val timestamp: Long = 0L,
)
