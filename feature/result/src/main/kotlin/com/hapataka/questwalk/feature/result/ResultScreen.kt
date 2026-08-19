package com.hapataka.questwalk.feature.result

import android.view.MotionEvent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
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
import com.google.maps.android.compose.Circle
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            is UiState.Failure -> ErrorContent(error = uiState.error)
            is UiState.Idle -> Unit
            // 데이터를 기다리는 동안에도 화면을 구성해 지도 초기화를 함께 진행시킨다.
            // 둘을 순서대로 하면 네트워크 대기와 지도 준비가 그대로 더해진다
            is UiState.Loading -> ResultContent(data = null)
            is UiState.Success -> ResultContent(data = uiState.data)
        }
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
private fun ResultContent(data: ResultUiState?) {
    val scrollState = rememberScrollState()
    var isMapGestureActive by remember { mutableStateOf(false) }
    var mapGestureResetKey by remember { mutableStateOf(0) }
    var isMapReady by remember { mutableStateOf(false) }

    // 데이터와 지도가 둘 다 준비돼야 한 번에 공개한다
    val isReady = data != null && isMapReady
    val shownData = data ?: ResultUiState()

    LaunchedEffect(mapGestureResetKey) {
        if (isMapGestureActive) {
            delay(MAP_GESTURE_SCROLL_LOCK_MILLIS)
            isMapGestureActive = false
        }
    }

    // 지도가 끝내 준비됐다고 알려오지 않아도 화면이 갇히지 않게 한다
    LaunchedEffect(Unit) {
        delay(MAP_READY_TIMEOUT_MILLIS)
        isMapReady = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState, enabled = !isMapGestureActive && isReady),
    ) {
        ResultMapSection(
            route = shownData.route,
            successLocation = shownData.successLocation,
            isDataReady = data != null,
            onMapReadyChange = { ready ->
                if (ready && !isMapReady) {
                    isMapReady = true
                }
            },
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
                text = convertTime(shownData.duration),
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "모험한 거리",
                fontSize = 14.sp,
                color = Color.Gray,
            )
            Text(
                text = convertKm(shownData.distance),
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
                    text = "${shownData.step}걸음  ",
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = convertKcal(shownData.step),
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
                    text = shownData.keyword,
                    fontSize = 24.sp,
                )
                Text(
                    text = "해결 인원 ${shownData.successCount}명",
                    style = MaterialTheme.typography.titleSmall,
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
            ResultQuestImageSection(imageUrl = shownData.imageUrl)

            if (shownData.otherImages.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                ResultOtherImagesSection(images = shownData.otherImages)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

        // 지도가 준비되기 전에 반쯤 그려진 화면을 보여주지 않는다.
        // 지도는 아래에서 이미 구성돼 준비를 진행하는 중이다
        if (!isReady) {
            ResultPreparingOverlay()
        }
    }
}

@Composable
private fun ResultPreparingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = MainPurple)
    }
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
private fun ResultMapSection(
    route: List<Location>,
    successLocation: Location?,
    isDataReady: Boolean,
    onMapReadyChange: (Boolean) -> Unit,
    onMapGestureActiveChange: (Boolean) -> Unit,
) {
    // 거리 필터와 RDP 단순화는 점이 많으면 무겁다.
    // remember 안에서 하면 컴포지션 중, 즉 메인 스레드에서 돌기 때문에 밖으로 뺀다
    val displayRoute by produceState<List<LatLng>?>(initialValue = null, route) {
        value = withContext(Dispatchers.Default) {
            route
                .toDisplayRoute()
                .map { LatLng(it.latitude.toDouble(), it.longitude.toDouble()) }
                .resampleRouteLatLngs(DISPLAY_ROUTE_POINT_COUNT)
        }
    }
    val routeLatLngs = displayRoute ?: emptyList()
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

    // 등속으로 점을 하나씩 더하면 기계적이라 진행률을 이징으로 굴린다
    val routeProgress = remember(routeLatLngs) { Animatable(0f) }
    val markerDropProgress = remember(routeLatLngs, successLatLng) { Animatable(0f) }
    val successRipple = remember(routeLatLngs, successLatLng) { Animatable(0f) }
    var isSuccessMarkerVisible by remember(routeLatLngs, successLatLng) { mutableStateOf(false) }
    var isMapLoaded by remember { mutableStateOf(false) }
    var isCameraFitComplete by remember(routeLatLngs) { mutableStateOf(false) }

    // 진행률을 그대로 읽으면 매 프레임 리컴포지션이 일어나고,
    // 그때마다 점 목록을 새로 만들어 지도에 통째로 다시 넘기게 된다.
    // 계단으로 끊어 갱신 횟수를 프레임 수와 분리한다
    val animatedRoutePointCount by remember(routeLatLngs) {
        derivedStateOf {
            if (routeLatLngs.size < 2) {
                routeLatLngs.size
            } else {
                val stepped =
                    (routeProgress.value * ROUTE_ANIMATION_STEPS).toInt() / ROUTE_ANIMATION_STEPS
                (1 + stepped * (routeLatLngs.size - 1))
                    .roundToInt()
                    .coerceIn(1, routeLatLngs.size)
            }
        }
    }
    val animatedRouteLatLngs = remember(routeLatLngs, animatedRoutePointCount) {
        routeLatLngs.take(animatedRoutePointCount)
    }
    val isRouteDrawing by remember(routeLatLngs) {
        derivedStateOf {
            routeLatLngs.size >= 2 && routeProgress.value > 0f && routeProgress.value < 1f
        }
    }

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

    if (isDataReady && displayRoute != null && routeLatLngs.isEmpty() && successLatLng == null) {
        // 지도를 아예 띄우지 않는 경우라 기다릴 것이 없다
        LaunchedEffect(Unit) { onMapReadyChange(true) }
        ResultPlaceholder(
            text = "이동 경로가 없습니다.",
            modifier = Modifier
                .fillMaxWidth()
                .height(356.dp),
        )
        return
    }

    val routeBounds = remember(routeLatLngs, successLatLng) {
        if (routeLatLngs.isEmpty() && successLatLng == null) {
            // 경로 계산이 끝나기 전이라 포함할 점이 없다. 빈 빌더는 예외를 던지므로 기본값을 쓴다
            LatLngBounds.builder().include(DEFAULT_MAP_CENTER).build()
        } else {
            buildRouteBounds(routeLatLngs, successLatLng)
        }
    }

    // 화면에 보이는 범위에 비례한 높이에서 떨어뜨려야 줌 배율과 무관하게 같은 연출이 나온다
    val markerDropOffset = remember(routeBounds) {
        val latSpan = routeBounds.northeast.latitude - routeBounds.southwest.latitude
        (latSpan * MARKER_DROP_HEIGHT_RATIO).coerceAtLeast(MARKER_DROP_MIN_LAT_OFFSET)
    }
    // 원 반지름은 미터 단위라 경로 크기에 맞춰야 어떤 경로에서도 비슷하게 보인다
    val baseMarkRadius = remember(routeBounds) {
        val latSpanMeters =
            (routeBounds.northeast.latitude - routeBounds.southwest.latitude) * METERS_PER_LAT_DEGREE
        (latSpanMeters * MAP_MARK_RADIUS_RATIO).coerceIn(MAP_MARK_MIN_RADIUS, MAP_MARK_MAX_RADIUS)
    }

    val headPulseRaw = rememberInfiniteTransition(label = "headPulse").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(HEAD_PULSE_DURATION_MILLIS, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "headPulseValue",
    )
    val headPulse by remember(headPulseRaw) {
        derivedStateOf { (headPulseRaw.value * HEAD_PULSE_STEPS).toInt() / HEAD_PULSE_STEPS }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = if (routeLatLngs.isNotEmpty()) {
            CameraPosition.fromLatLngZoom(routeBounds.center, 15f)
        } else if (successLatLng != null) {
            CameraPosition.fromLatLngZoom(successLatLng, 16f)
        } else {
            CameraPosition.fromLatLngZoom(DEFAULT_MAP_CENTER, 15f)
        }
    }
    val mapUiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = false,
        )
    }

    LaunchedEffect(isMapLoaded) {
        if (isMapLoaded) onMapReadyChange(true)
    }

    LaunchedEffect(isMapLoaded, routeBounds) {
        if (!isMapLoaded) return@LaunchedEffect

        isCameraFitComplete = false
        if (routeLatLngs.size >= 2) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngBounds(
                    routeBounds,
                    MAP_ROUTE_START_BOUNDS_PADDING,
                ),
                durationMs = MAP_ROUTE_CAMERA_ANIMATION_MILLIS,
            )
        }
        isCameraFitComplete = true
    }

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
            // 파동은 착지와 함께 퍼지되 다음 동작을 붙잡지 않는다
            launch {
                successRipple.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = SUCCESS_RIPPLE_DURATION_MILLIS,
                        easing = LinearOutSlowInEasing,
                    ),
                )
            }
            delay(MARKER_DROP_HOLD_MILLIS)
        }

        suspend fun drawRouteTo(target: Float) {
            val distance = target - routeProgress.value
            if (distance <= 0f) return
            routeProgress.animateTo(
                targetValue = target,
                animationSpec = tween(
                    durationMillis = (ROUTE_ANIMATION_DURATION_MILLIS * distance)
                        .toInt()
                        .coerceAtLeast(ROUTE_ANIMATION_MIN_SEGMENT_MILLIS),
                    easing = ROUTE_DRAW_EASING,
                ),
            )
        }

        delay(ROUTE_ANIMATION_START_DELAY_MILLIS)

        if (!hasRoute) {
            dropSuccessMarker()
            return@LaunchedEffect
        }

        routeProgress.snapTo(0f)
        var hasDroppedMarker = successLatLng == null || successPointIndex <= 0

        if (!hasDroppedMarker) {
            val successFraction =
                (successPointIndex.toFloat() / (routeLatLngs.size - 1)).coerceIn(0f, 1f)
            drawRouteTo(successFraction)
            hasDroppedMarker = true
            dropSuccessMarker()
        }

        drawRouteTo(1f)

        if (!hasDroppedMarker) {
            dropSuccessMarker()
        }

        // 상대 줌으로 당기면 경로가 화면을 벗어난다. 경로 범위에 다시 맞춰 마무리한다
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngBounds(
                routeBounds,
                MAP_ROUTE_BOUNDS_PADDING,
            ),
            durationMs = FINISH_ZOOM_DURATION_MILLIS,
        )
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

        // 출발점. 경로가 겹쳐도 기준이 되도록 그리는 내내 남겨둔다
        routeLatLngs.firstOrNull()?.let { startLatLng ->
            Circle(
                center = startLatLng,
                radius = baseMarkRadius * START_MARK_RADIUS_SCALE,
                fillColor = Color.White,
                strokeColor = ROUTE_OUTLINE_COLOR,
                strokeWidth = 6f,
                zIndex = START_MARK_Z_INDEX,
            )
        }

        // 그려지는 끝을 따라가는 선두 점
        if (isRouteDrawing) {
            animatedRouteLatLngs.lastOrNull()?.let { headLatLng ->
                Circle(
                    center = headLatLng,
                    radius = baseMarkRadius * (1f + headPulse * HEAD_PULSE_RADIUS_SCALE),
                    fillColor = MainPurple.copy(alpha = (1f - headPulse) * HEAD_PULSE_MAX_ALPHA),
                    strokeColor = Color.Transparent,
                    strokeWidth = 0f,
                    zIndex = HEAD_MARK_Z_INDEX,
                )
                Circle(
                    center = headLatLng,
                    radius = baseMarkRadius,
                    fillColor = MainPurple,
                    strokeColor = Color.White,
                    strokeWidth = 6f,
                    zIndex = HEAD_MARK_Z_INDEX + 1f,
                )
            }
        }

        // 핀이 꽂히는 순간 한 번 퍼지는 파동
        if (successLatLng != null && successRipple.value > 0f && successRipple.value < 1f) {
            Circle(
                center = successLatLng,
                radius = baseMarkRadius * (1f + successRipple.value * SUCCESS_RIPPLE_RADIUS_SCALE),
                fillColor = Color.Transparent,
                strokeColor = MainPurple.copy(alpha = 1f - successRipple.value),
                strokeWidth = 8f,
                zIndex = SUCCESS_RIPPLE_Z_INDEX,
            )
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

    // fold + 리스트 복사는 점 개수의 제곱만큼 일한다. 긴 경로에서 첫 진입이 끊기는 원인이었다
    val distanceFiltered = ArrayList<Location>(size)
    for (current in this) {
        val prev = distanceFiltered.lastOrNull()
        if (prev == null || prev.distanceTo(current) >= MIN_ROUTE_POINT_DISTANCE_METERS) {
            distanceFiltered.add(current)
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
private const val ROUTE_ANIMATION_DURATION_MILLIS = 2500
// 갱신을 이 횟수로 끊는다. 프레임마다 지도를 건드리지 않기 위한 값
private const val ROUTE_ANIMATION_STEPS = 100f
// 천천히 출발해 점점 빨라진다. 끝에서만 살짝 눕혀 급정지를 막는다
private val ROUTE_DRAW_EASING = CubicBezierEasing(0.5f, 0f, 0.85f, 1f)
private const val ROUTE_ANIMATION_MIN_SEGMENT_MILLIS = 320
private const val ROUTE_OUTLINE_Z_INDEX = 10f
private const val ROUTE_LINE_Z_INDEX = 11f
private const val MAP_ROUTE_BOUNDS_PADDING = 132
// 시작은 더 멀리서 잡아야 마지막에 당길 여유가 생긴다. 여백이 클수록 멀어진다
private const val MAP_ROUTE_START_BOUNDS_PADDING = 260
private const val MAP_ROUTE_CAMERA_ANIMATION_MILLIS = 650
private const val MAP_READY_TIMEOUT_MILLIS = 4000L
private const val SUCCESS_MARKER_Z_INDEX = 12f
private const val MARKER_DROP_DURATION_MILLIS = 520
private const val MARKER_DROP_HOLD_MILLIS = 260L
private const val MARKER_DROP_HEIGHT_RATIO = 0.45
private const val MARKER_DROP_MIN_LAT_OFFSET = 0.0012
private const val METERS_PER_LAT_DEGREE = 111_000.0
private const val MAP_MARK_RADIUS_RATIO = 0.018
private const val MAP_MARK_MIN_RADIUS = 4.0
private const val MAP_MARK_MAX_RADIUS = 26.0
private const val START_MARK_RADIUS_SCALE = 0.85
private const val START_MARK_Z_INDEX = 11.5f
private const val HEAD_MARK_Z_INDEX = 13f
private const val HEAD_PULSE_DURATION_MILLIS = 1100
private const val HEAD_PULSE_RADIUS_SCALE = 2.4f
private const val HEAD_PULSE_MAX_ALPHA = 0.45f
private const val HEAD_PULSE_STEPS = 20f
private const val SUCCESS_RIPPLE_DURATION_MILLIS = 760
private const val SUCCESS_RIPPLE_RADIUS_SCALE = 4.5f
private const val SUCCESS_RIPPLE_Z_INDEX = 11.8f
private const val FINISH_ZOOM_DURATION_MILLIS = 700
// 흰색에서 시작하므로 외곽선을 어둡게 둬야 앞부분이 배경에 묻히지 않는다
private val DEFAULT_MAP_CENTER = LatLng(37.5665, 126.9780)
private val SkeletonGray = Color(0x14262626)
private val ROUTE_OUTLINE_COLOR = Color(0x8A262626)
private val ROUTE_GRADIENT_START_COLOR = Color.White
private val ROUTE_GRADIENT_END_COLOR = MainPurple
