package com.hapataka.questwalk.data.dto.station

data class Body(
    val items: List<com.hapataka.questwalk.data.dto.station.Item>,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)