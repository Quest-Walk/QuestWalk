package com.hapataka.questwalk.core.data.repository

import com.hapataka.questwalk.core.data.di.ApplicationScope
import com.hapataka.questwalk.core.domain.repository.LocationRepository
import com.hapataka.questwalk.core.domain.repository.PlaySessionRepository
import com.hapataka.questwalk.core.model.Location
import com.hapataka.questwalk.core.model.PlaySession
import com.hapataka.questwalk.core.model.PlayState
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

@Singleton
class DefaultPlaySessionRepository @Inject constructor(
    private val locationRepository: LocationRepository,
    @ApplicationScope private val externalScope: CoroutineScope,
) : PlaySessionRepository {

    private val _sessionState = MutableStateFlow(PlaySession())
    override val sessionState: StateFlow<PlaySession> = _sessionState.asStateFlow()

    private var timerJob: Job? = null
    private var locationTrackingJob: Job? = null

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

    override fun markSuccess(location: Location) {
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
        locationTrackingJob?.cancel()
        locationTrackingJob = externalScope.launch {
            locationRepository.getLocationUpdates().collect { locationUpdate ->
                val currentRoute = _sessionState.value.route
                val newRoute = currentRoute + locationUpdate.location
                val newDistance = _sessionState.value.distance + locationUpdate.distance.coerceAtMost(30f)

                _sessionState.update {
                    it.copy(
                        route = newRoute,
                        distance = newDistance,
                    )
                }
            }
        }
    }

    private fun stopLocationTracking() {
        locationTrackingJob?.cancel()
        locationTrackingJob = null
    }
}
