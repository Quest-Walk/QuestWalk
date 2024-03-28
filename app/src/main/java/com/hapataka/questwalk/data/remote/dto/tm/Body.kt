package com.hapataka.questwalk.data.remote.dto.tm

data class Body(
    val items: List<Item>,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)