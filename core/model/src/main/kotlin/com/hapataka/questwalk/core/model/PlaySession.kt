package com.hapataka.questwalk.core.model

data class PlaySession(
    val playState: PlayState = PlayState.STOPPED,
    val keyword: String = "",
    val level: Int = 0,
    val duration: Long = 0L,
    val distance: Float = 0f,
    val steps: Long = 0L,
    val route: List<Pair<Float, Float>> = emptyList(),
    val successLocation: Pair<Float, Float>? = null,
) {
    val isPlaying: Boolean get() = playState == PlayState.PLAYING
    val isStopped: Boolean get() = playState == PlayState.STOPPED
    val isSuccess: Boolean get() = playState == PlayState.SUCCESS
}
