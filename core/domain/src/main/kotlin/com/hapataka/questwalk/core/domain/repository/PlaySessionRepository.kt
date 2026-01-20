package com.hapataka.questwalk.core.domain.repository

import com.hapataka.questwalk.core.model.Location
import com.hapataka.questwalk.core.model.PlaySession
import kotlinx.coroutines.flow.StateFlow

interface PlaySessionRepository {
    val sessionState: StateFlow<PlaySession>

    fun startSession(keyword: String, level: Int)
    fun stopSession()
    fun markSuccess(location: Location)
    fun incrementStep()
    fun resetSession()
}
