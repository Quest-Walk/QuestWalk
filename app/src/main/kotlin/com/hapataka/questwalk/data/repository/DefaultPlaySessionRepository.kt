package com.hapataka.questwalk.data.repository

import com.hapataka.questwalk.core.domain.repository.PlaySessionRepository
import com.hapataka.questwalk.core.model.PlaySession
import com.hapataka.questwalk.core.model.PlayState
import com.hapataka.questwalk.data.di.ApplicationScope
import com.hapataka.questwalk.domain.repository.LocationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

// TODO: LocationRepository가 core 모듈로 이동하면 이 클래스도 core/data로 이동
@Singleton
class DefaultPlaySessionRepository @Inject constructor(
    private val locationRepository: LocationRepository,
    @ApplicationScope private val externalScope: CoroutineScope,
) : PlaySessionRepository {

    private val _sessionState = MutableStateFlow(PlaySession())
    override val sessionState: StateFlow<PlaySession> = _sessionState.asStateFlow()

    private var timerJob: Job? = null

    override fun startSession(keyword: String, level: Int) {
        _sessionState.update {
            PlaySession(
                playState = PlayState.PLAYING,
                keyword = keyword,
                level = level,
                duration = 0L,
                distance = 0f,
                steps = 0L,
                route = emptyList(),
                successLocation = null,
            )
        }
        startTimer()
        startLocationTracking()
    }

    override fun stopSession() {
        stopTimer()
        stopLocationTracking()
        _sessionState.update { it.copy(playState = PlayState.STOPPED) }
    }

    override fun markSuccess(location: Pair<Float, Float>) {
        _sessionState.update {
            it.copy(
                playState = PlayState.SUCCESS,
                successLocation = location,
            )
        }
    }

    override fun incrementStep() {
        _sessionState.update { it.copy(steps = it.steps + 1) }
    }

    override fun resetSession() {
        stopTimer()
        stopLocationTracking()
        _sessionState.value = PlaySession()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = externalScope.launch {
            while (true) {
                delay(1000L)
                _sessionState.update { it.copy(duration = it.duration + 1) }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun startLocationTracking() {
        locationRepository.startRequest { locationEntity ->
            val currentRoute = _sessionState.value.route
            val newRoute = currentRoute + locationEntity.location
            val newDistance = _sessionState.value.distance + locationEntity.distance.coerceAtMost(30f)

            _sessionState.update {
                it.copy(
                    route = newRoute,
                    distance = newDistance,
                )
            }
        }
    }

    private fun stopLocationTracking() {
        locationRepository.finishRequest()
    }
}
