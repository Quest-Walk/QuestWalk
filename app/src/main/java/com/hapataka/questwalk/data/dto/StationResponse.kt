package com.hapataka.questwalk.data.dto

data class StationResponse(
    val station: Station
)

data class Station(
    val stationBody: StationBody,
)

data class StationBody(
    val stationItems: List<StationItem>
)

data class StationItem(
    val addr: String,
    val stationCode: String,
    val stationName: String,
    val tm: Double
)