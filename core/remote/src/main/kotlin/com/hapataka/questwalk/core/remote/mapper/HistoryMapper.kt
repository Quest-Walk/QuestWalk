package com.hapataka.questwalk.core.remote.mapper

import android.util.Log
import com.hapataka.questwalk.core.model.History
import com.hapataka.questwalk.core.remote.model.QuestResultDto
import com.hapataka.questwalk.core.remote.util.decryptECB
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

internal fun QuestResultDto.toModel(key: String): History.QuestResult {
    return History.QuestResult(
        userId = this.userId,
        registerAt = LocalDateTime.parse(this.registerAt),
        questKeyword = this.questKeyword,
        duration = this.duration,
        distance = this.distance,
        step = this.step,
        isSuccess = this.isSuccess,
        route = this.route.toRoute(key),
        successLocation = this.successLocation?.toLocation(key),
        imageUrl = this.imageUrl,
    )
}

private fun String.toRoute(key: String): MutableList<Pair<Float, Float>> {
    val json = Json {
        ignoreUnknownKeys = true
    }

    Log.w("fatal", this.decryptECB(key).replace("\"", ""))

    return json.decodeFromString(this.decryptECB(key).replace("\"", ""))
}

private fun String.toLocation(key: String): Pair<Float, Float> {
    val json = Json {
        ignoreUnknownKeys = true
    }

    return json.decodeFromString(this.decryptECB(key).replace("\"", ""))
}