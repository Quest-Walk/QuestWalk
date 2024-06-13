package com.hapataka.questwalk.data.dto

import com.google.gson.annotations.SerializedName

data class StationResponse(
    @SerializedName("response")
    val station: Station
)

data class Station(
    @SerializedName("body")
    val stationBody: StationBody,
)

data class StationBody(
    @SerializedName("items")
    val stationItems: List<StationItem>
)

data class StationItem(
    val addr: String,
    val stationName: String,
    val tm: Double
)