package com.hapataka.questwalk.data.dto.tm

data class Body(
    val items: List<com.hapataka.questwalk.data.dto.tm.Item>,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)