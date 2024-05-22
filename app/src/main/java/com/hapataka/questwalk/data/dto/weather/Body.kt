package com.hapataka.questwalk.data.dto.weather

data class Body(
    val dataType: String,
    val items: com.hapataka.questwalk.data.dto.weather.Items,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)