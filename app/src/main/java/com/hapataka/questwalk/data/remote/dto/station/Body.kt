package com.hapataka.questwalk.data.remote.dto.station

data class Body(
    val items: List<Item>,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)