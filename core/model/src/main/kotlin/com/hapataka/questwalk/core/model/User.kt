package com.hapataka.questwalk.core.model

data class User(
    val userId: String,
    var userName: String,
    var characterType: Int,
    var totalTime: Long,
    var totalDistance: Float,
    var totalStep: Long,
)