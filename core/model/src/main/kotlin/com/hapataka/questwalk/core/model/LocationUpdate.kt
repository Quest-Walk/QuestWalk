package com.hapataka.questwalk.core.model

/**
 * 실시간 위치 업데이트 정보
 *
 * @property location 현재 위치 (위도, 경도)
 * @property distance 이전 위치로부터 이동한 거리 (미터)
 */
data class LocationUpdate(
    val location: Location,
    val distance: Float,
)
