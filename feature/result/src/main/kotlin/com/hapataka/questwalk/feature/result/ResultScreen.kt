package com.hapataka.questwalk.feature.result

import android.view.MotionEvent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.SubcomposeAsyncImage
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.StrokeStyle
import com.google.android.gms.maps.model.StyleSpan
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.hapataka.questwalk.core.designsystem.component.QuestWalkTopAppBar
import com.hapataka.questwalk.core.designsystem.theme.MainPurple
import com.hapataka.questwalk.core.model.Location
import com.hapataka.questwalk.core.ui.LocalPaddingValues
import com.hapataka.questwalk.core.ui.UiState
import java.text.DecimalFormat
import kotlin.math.roundToInt
import kotlinx.coroutines.delay

@Composable
internal fun ResultRoute(
    padding: PaddingValues = LocalPaddingValues.current,
    viewModel: ResultViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ResultScreen(
        uiState = uiState,
        padding = padding,
        onBackClick = onBackClick,
    )
}

@Composable
private fun ResultScreen(
    uiState: UiState<ResultUiState>,
    padding: PaddingValues,
    onBackClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
    ) {
        QuestWalkTopAppBar(
            title = "퀘스트 결과",
            contentColor = MainPurple,
            leadingIcon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            onClickLeadingIcon = onBackClick,
        )

        when (uiState) {
            is UiState.Loading -> LoadingContent()
            is UiState.Success -> ResultContent(data = uiState.data)
            is UiState.Failure -> ErrorContent(error = uiState.error)
            is UiState.Idle -> Unit
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = MainPurple)
    }
}

@Composable
private fun ErrorContent(error: Throwable) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = error.message ?: "오류가 발생했습니다.",
            color = Color.Gray,
        )
    }
}

@Composable
private fun ResultContent(data: ResultUiState) {
    val scrollState = rememberScrollState()
    var isMapGestureActive by remember { mutableStateOf(false) }
    var mapGestureResetKey by remember { mutableStateOf(0) }

    LaunchedEffect(mapGestureResetKey) {
        if (isMapGestureActive) {
            delay(MAP_GESTURE_SCROLL_LOCK_MILLIS)
            isMapGestureActive = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState, enabled = !isMapGestureActive),
    ) {
        ResultMapSection(
            route = data.route,
            successLocation = data.successLocation,
            onMapGestureActiveChange = { active ->
                isMapGestureActive = active
                if (active) {
                    mapGestureResetKey += 1
                }
            },
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "모험한 시간",
                fontSize = 14.sp,
                color = Color.Gray,
            )
            Text(
                text = convertTime(data.duration),
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "모험한 거리",
                fontSize = 14.sp,
                color = Color.Gray,
            )
            Text(
                text = convertKm(data.distance),
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "총 걸음수",
                fontSize = 14.sp,
                color = Color.Gray,
            )
            Row(
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = "${data.step}걸음  ",
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = convertKcal(data.step),
                    style = MaterialTheme.typography.titleSmall,
                )
            }

            Spacer(modifier = Modifier.height(50.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = data.keyword,
                    fontSize = 24.sp,
                )
                Text(
                    text = "해결 인원 ${data.successCount}명",
                    style = MaterialTheme.typography.titleSmall,
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
            ResultQuestImageSection(imageUrl = data.imageUrl)

            if (data.otherImages.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                ResultOtherImagesSection(images = data.otherImages)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
private fun ResultMapSection(
    route: List<Location>,
    successLocation: Location?,
    onMapGestureActiveChange: (Boolean) -> Unit,
) {
    val routeLatLngs = remember(route) {
        route
            .toDisplayRoute()
            .map { LatLng(it.latitude.toDouble(), it.longitude.toDouble()) }
            .resampleRouteLatLngs(DISPLAY_ROUTE_POINT_COUNT)
    }
    val successLatLng = remember(successLocation) {
        successLocation?.let {
            LatLng(it.latitude.toDouble(), it.longitude.toDouble())
        }
    }
    // 애니메이션이 이 지점을 지날 때 성공 마커를 떨어뜨린다
    val successPointIndex = remember(routeLatLngs, successLatLng) {
        if (successLatLng == null) {
            -1
        } else {
            routeLatLngs.indices.minByOrNull { index ->
                val point = routeLatLngs[index]
                val latDiff = point.latitude - successLatLng.latitude
                val lngDiff = point.longitude - successLatLng.longitude
                latDiff * latDiff + lngDiff * lngDiff
            } ?: -1
        }
    }
    var animatedRoutePointCount by remember(routeLatLngs) {
        mutableStateOf(if (routeLatLngs.size >= 2) 1 else routeLatLngs.size)
    }
    var isMapLoaded by remember(routeLatLngs) { mutableStateOf(false) }
    var isCameraFitComplete by remember(routeLatLngs) { mutableStateOf(false) }
    var isSuccessMarkerVisible by remember(routeLatLngs, successLatLng) { mutableStateOf(false) }
    val markerDropProgress = remember(routeLatLngs, successLatLng) { Animatable(0f) }

    LaunchedEffect(routeLatLngs, successLatLng, isCameraFitComplete) {
        val hasRoute = routeLatLngs.size >= 2
        if (!hasRoute && successLatLng == null) return@LaunchedEffect
        if (hasRoute && !isCameraFitComplete) return@LaunchedEffect

        // 핀이 꽂히는 동안에는 경로를 멈춰야 두 동작이 섞이지 않는다
        suspend fun dropSuccessMarker() {
            isSuccessMarkerVisible = true
            markerDropProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = MARKER_DROP_DURATION_MILLIS,
                    easing = EaseOutBack,
                ),
            )
            delay(MARKER_DROP_HOLD_MILLIS)
        }

        delay(ROUTE_ANIMATION_START_DELAY_MILLIS)

        if (!hasRoute) {
            dropSuccessMarker()
            return@LaunchedEffect
        }

        animatedRoutePointCount = 1
        var hasDroppedMarker = successLatLng == null || successPointIndex < 0

        val pointsPerFrame = maxOf(1, routeLatLngs.size / ROUTE_ANIMATION_MAX_FRAMES)
        while (animatedRoutePointCount < routeLatLngs.size) {
            delay(ROUTE_ANIMATION_FRAME_MILLIS)
            animatedRoutePointCount = minOf(
                routeLatLngs.size,
                animatedRoutePointCount + pointsPerFrame,
            )

            if (!hasDroppedMarker && animatedRoutePointCount > successPointIndex) {
                hasDroppedMarker = true
                dropSuccessMarker()
            }
        }

        if (!hasDroppedMarker) {
            dropSuccessMarker()
        }
    }
    val animatedRouteLatLngs = routeLatLngs.take(animatedRoutePointCount)

    // 겹치는 구간에서도 진행 방향이 보이도록 시작에서 끝으로 색을 옮긴다.
    // 머리 색을 전체 대비 진행률로 잡아야 그라데이션이 경로 전체에 고정된다
    val routeGradientSpans = remember(animatedRoutePointCount, routeLatLngs.size) {
        val drawnSegments = animatedRoutePointCount - 1
        if (drawnSegments < 1) {
            emptyList()
        } else {
            val totalSegments = (routeLatLngs.size - 1).coerceAtLeast(1)
            val progress = drawnSegments.toFloat() / totalSegments
            val headColor = lerp(ROUTE_GRADIENT_START_COLOR, ROUTE_GRADIENT_END_COLOR, progress)
            listOf(
                StyleSpan(
                    StrokeStyle
                        .gradientBuilder(ROUTE_GRADIENT_START_COLOR.toArgb(), headColor.toArgb())
                        .build(),
                    drawnSegments.toDouble(),
                )
            )
        }
    }

    if (routeLatLngs.isEmpty() && successLatLng == null) {
        ResultPlaceholder(
            text = "이동 경로가 없습니다.",
            modifier = Modifier
                .fillMaxWidth()
                .height(356.dp),
        )
        return
    }

    val routeBounds = remember(routeLatLngs, successLatLng) {
        buildRouteBounds(routeLatLngs, successLatLng)
    }

    // 화면에 보이는 범위에 비례한 높이에서 떨어뜨려야 줌 배율과 무관하게 같은 연출이 나온다
    val markerDropOffset = remember(routeBounds) {
        val latSpan = routeBounds.northeast.latitude - routeBounds.southwest.latitude
        (latSpan * MARKER_DROP_HEIGHT_RATIO).coerceAtLeast(MARKER_DROP_MIN_LAT_OFFSET)
    }
    val cameraPositionState = rememberCameraPositionState {
        position = if (routeLatLngs.isNotEmpty()) {
            CameraPosition.fromLatLngZoom(routeBounds.center, 15f)
        } else if (successLatLng != null) {
            CameraPosition.fromLatLngZoom(successLatLng, 16f)
        } else {
            CameraPosition.fromLatLngZoom(LatLng(37.5665, 126.9780), 15f)
        }
    }
    val mapUiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = false,
        )
    }

    LaunchedEffect(isMapLoaded, routeBounds) {
        if (!isMapLoaded) return@LaunchedEffect

        isCameraFitComplete = false
        if (routeLatLngs.size >= 2) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngBounds(
                    routeBounds,
                    MAP_ROUTE_BOUNDS_PADDING,
                ),
                durationMs = MAP_ROUTE_CAMERA_ANIMATION_MILLIS,
            )
        }
        isCameraFitComplete = true
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(356.dp)
            .pointerInteropFilter { event ->
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN,
                    MotionEvent.ACTION_POINTER_DOWN,
                    MotionEvent.ACTION_MOVE,
                    -> onMapGestureActiveChange(true)

                    MotionEvent.ACTION_UP,
                    MotionEvent.ACTION_CANCEL,
                    MotionEvent.ACTION_POINTER_UP,
                    -> onMapGestureActiveChange(false)
                }
                false
            },
        cameraPositionState = cameraPositionState,
        uiSettings = mapUiSettings,
        onMapLoaded = {
            isMapLoaded = true
        },
    ) {
        if (animatedRouteLatLngs.size >= 2) {
            Polyline(
                points = animatedRouteLatLngs,
                color = ROUTE_OUTLINE_COLOR,
                width = 24f,
                zIndex = ROUTE_OUTLINE_Z_INDEX,
            )
            // spans 오버로드에는 color 인자가 없다. 그라데이션을 못 만들 때만 단색으로 그린다
            if (routeGradientSpans.isEmpty()) {
                Polyline(
                    points = animatedRouteLatLngs,
                    color = MainPurple,
                    width = 16f,
                    zIndex = ROUTE_LINE_Z_INDEX,
                )
            } else {
                Polyline(
                    points = animatedRouteLatLngs,
                    spans = routeGradientSpans,
                    width = 16f,
                    zIndex = ROUTE_LINE_Z_INDEX,
                )
            }
        }

        if (successLatLng != null && isSuccessMarkerVisible) {
            val dropProgress = markerDropProgress.value
            Marker(
                state = MarkerState(
                    position = LatLng(
                        successLatLng.latitude + markerDropOffset * (1f - dropProgress),
                        successLatLng.longitude,
                    )
                ),
                alpha = dropProgress.coerceIn(0f, 1f),
                zIndex = SUCCESS_MARKER_Z_INDEX,
                title = "QUEST",
            )
        }
    }
}

@Composable
private fun ResultQuestImageSection(imageUrl: String) {
    if (imageUrl.isBlank()) {
        ResultPlaceholder(
            text = "인증 사진이 없습니다.",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
        )
        return
    }

    SubcomposeAsyncImage(
        model = imageUrl,
        contentDescription = "Quest Image",
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp)),
        contentScale = ContentScale.Crop,
        loading = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MainPurple,
                )
            }
        },
        error = {
            ResultPlaceholder(
                text = "사진을 불러오지 못했습니다.",
                modifier = Modifier.fillMaxSize(),
            )
        },
    )
}

private fun List<Location>.toDisplayRoute(): List<Location> {
    if (size < 3) return this

    val distanceFiltered = fold(emptyList<Location>()) { accepted, current ->
        val prev = accepted.lastOrNull()
        if (prev == null || prev.distanceTo(current) >= MIN_ROUTE_POINT_DISTANCE_METERS) {
            accepted + current
        } else {
            accepted
        }
    }

    return distanceFiltered.simplifyRoute(ROUTE_SIMPLIFY_TOLERANCE_METERS)
}

private fun Location.distanceTo(other: Location): Float {
    val results = FloatArray(1)
    android.location.Location.distanceBetween(
        latitude.toDouble(),
        longitude.toDouble(),
        other.latitude.toDouble(),
        other.longitude.toDouble(),
        results,
    )
    return results[0]
}

private fun List<LatLng>.resampleRouteLatLngs(targetPointCount: Int): List<LatLng> {
    if (size < 2 || targetPointCount < 2) return this

    val segmentDistances = zipWithNext { start, end -> start.distanceTo(end).toDouble() }
    val totalDistance = segmentDistances.sum()
    if (totalDistance <= 0.0) return this

    val resampled = mutableListOf(first())
    var segmentIndex = 0
    var distanceBeforeSegment = 0.0

    for (pointIndex in 1 until targetPointCount - 1) {
        val targetDistance = totalDistance * pointIndex / (targetPointCount - 1)

        while (
            segmentIndex < segmentDistances.lastIndex &&
            distanceBeforeSegment + segmentDistances[segmentIndex] < targetDistance
        ) {
            distanceBeforeSegment += segmentDistances[segmentIndex]
            segmentIndex += 1
        }

        val segmentDistance = segmentDistances[segmentIndex]
        val ratio = if (segmentDistance == 0.0) {
            0.0
        } else {
            (targetDistance - distanceBeforeSegment) / segmentDistance
        }
        resampled += this[segmentIndex].interpolateTo(this[segmentIndex + 1], ratio)
    }

    resampled += last()
    return resampled
}

private fun LatLng.distanceTo(other: LatLng): Float {
    val results = FloatArray(1)
    android.location.Location.distanceBetween(
        latitude,
        longitude,
        other.latitude,
        other.longitude,
        results,
    )
    return results[0]
}

private fun LatLng.interpolateTo(other: LatLng, ratio: Double): LatLng {
    return LatLng(
        latitude + ((other.latitude - latitude) * ratio),
        longitude + ((other.longitude - longitude) * ratio),
    )
}

private fun List<Location>.simplifyRoute(toleranceMeters: Float): List<Location> {
    if (size < 3) return this

    var maxDistance = 0f
    var index = 0
    val start = first()
    val end = last()

    for (i in 1 until lastIndex) {
        val distance = this[i].perpendicularDistanceTo(start, end)
        if (distance > maxDistance) {
            maxDistance = distance
            index = i
        }
    }

    return if (maxDistance > toleranceMeters) {
        val firstSegment = subList(0, index + 1).simplifyRoute(toleranceMeters)
        val secondSegment = subList(index, size).simplifyRoute(toleranceMeters)
        firstSegment.dropLast(1) + secondSegment
    } else {
        listOf(start, end)
    }
}

private fun Location.perpendicularDistanceTo(start: Location, end: Location): Float {
    val originLatitude = start.latitude.toDouble()
    val startPoint = start.toMeterPoint(originLatitude)
    val endPoint = end.toMeterPoint(originLatitude)
    val currentPoint = toMeterPoint(originLatitude)
    val dx = endPoint.x - startPoint.x
    val dy = endPoint.y - startPoint.y

    if (dx == 0.0 && dy == 0.0) {
        return distanceTo(start)
    }

    val numerator = kotlin.math.abs(
        (dy * currentPoint.x) -
            (dx * currentPoint.y) +
            (endPoint.x * startPoint.y) -
            (endPoint.y * startPoint.x)
    )
    val denominator = kotlin.math.sqrt((dy * dy) + (dx * dx))
    return (numerator / denominator).toFloat()
}

private fun Location.toMeterPoint(originLatitude: Double): MeterPoint {
    val metersPerDegreeLatitude = 111_320.0
    val metersPerDegreeLongitude = 111_320.0 * kotlin.math.cos(Math.toRadians(originLatitude))
    return MeterPoint(
        x = longitude.toDouble() * metersPerDegreeLongitude,
        y = latitude.toDouble() * metersPerDegreeLatitude,
    )
}

private data class MeterPoint(
    val x: Double,
    val y: Double,
)

private fun buildRouteBounds(
    routeLatLngs: List<LatLng>,
    successLatLng: LatLng?,
): LatLngBounds {
    return LatLngBounds.builder().apply {
        routeLatLngs.forEach { include(it) }
        successLatLng?.let { include(it) }
    }.build()
}

@Composable
private fun ResultOtherImagesSection(images: List<String>) {
    val displayImages = images.filter { it.isNotBlank() }.take(4)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        displayImages.forEach { imageUrl ->
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = "Other User Image",
                modifier = Modifier
                    .weight(0.23f)
                    .height(76.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray),
                    )
                },
                error = {
                    ResultPlaceholder(
                        text = "",
                        modifier = Modifier.fillMaxSize(),
                    )
                },
            )
        }
        repeat(4 - displayImages.size) {
            Spacer(modifier = Modifier.weight(0.23f))
        }
    }
}

@Composable
private fun ResultPlaceholder(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE9E5F4)),
        contentAlignment = Alignment.Center,
    ) {
        if (text.isNotBlank()) {
            Text(
                text = text,
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun convertTime(durationSeconds: Long): String {
    val hours = durationSeconds / 3600
    val minutes = (durationSeconds % 3600) / 60
    val seconds = durationSeconds % 60
    return "${hours}시간 ${minutes}분 ${seconds}초"
}

private fun convertKm(distanceMeters: Float): String {
    val df = DecimalFormat("#.##")
    return if (distanceMeters >= 1000) {
        "${df.format(distanceMeters / 1000)}km"
    } else {
        "${distanceMeters.toInt()}m"
    }
}

private fun convertKcal(steps: Long): String {
    val kcal = (steps * 0.06f).roundToInt()
    return "${kcal}Kcal"
}

private const val MIN_ROUTE_POINT_DISTANCE_METERS = 4f
private const val ROUTE_SIMPLIFY_TOLERANCE_METERS = 4f
private const val MAP_GESTURE_SCROLL_LOCK_MILLIS = 900L
private const val DISPLAY_ROUTE_POINT_COUNT = 300
private const val ROUTE_ANIMATION_START_DELAY_MILLIS = 800L
private const val ROUTE_ANIMATION_FRAME_MILLIS = 32L
private const val ROUTE_ANIMATION_MAX_FRAMES = 78
private const val ROUTE_OUTLINE_Z_INDEX = 10f
private const val ROUTE_LINE_Z_INDEX = 11f
private const val MAP_ROUTE_BOUNDS_PADDING = 132
private const val MAP_ROUTE_CAMERA_ANIMATION_MILLIS = 650
private const val SUCCESS_MARKER_Z_INDEX = 12f
private const val MARKER_DROP_DURATION_MILLIS = 520
private const val MARKER_DROP_HOLD_MILLIS = 260L
private const val MARKER_DROP_HEIGHT_RATIO = 0.45
private const val MARKER_DROP_MIN_LAT_OFFSET = 0.0012
// 흰색에서 시작하므로 외곽선을 어둡게 둬야 앞부분이 배경에 묻히지 않는다
private val ROUTE_OUTLINE_COLOR = Color(0x8A262626)
private val ROUTE_GRADIENT_START_COLOR = Color.White
private val ROUTE_GRADIENT_END_COLOR = MainPurple
