package com.hapataka.questwalk.domain.entity

import androidx.resourceinspection.annotation.Attribute.IntMap

data class ForecastEntity(
    val fcstData: String,
    val fcstTime: String,
    val baseDate: String,
    val baseTime: String,
    val sky: Int,
    val precipType: Int,
    val temp: Int
)
