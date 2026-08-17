package com.hapataka.questwalk.feature.result

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.SubcomposeAsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        ResultMapSection(
            route = data.route,
            successLocation = data.successLocation,
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
private fun ResultMapSection(
    route: List<Location>,
    successLocation: Location?,
) {
    val routeLatLngs = route.map { LatLng(it.latitude.toDouble(), it.longitude.toDouble()) }
    val successLatLng = successLocation?.let {
        LatLng(it.latitude.toDouble(), it.longitude.toDouble())
    }

    val cameraPositionState = rememberCameraPositionState {
        position = if (routeLatLngs.isNotEmpty()) {
            val bounds = LatLngBounds.builder().apply {
                routeLatLngs.forEach { include(it) }
                successLatLng?.let { include(it) }
            }.build()
            CameraPosition.fromLatLngZoom(bounds.center, 15f)
        } else {
            CameraPosition.fromLatLngZoom(LatLng(37.5665, 126.9780), 15f)
        }
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(356.dp),
        cameraPositionState = cameraPositionState,
    ) {
        if (routeLatLngs.size >= 2) {
            Polyline(
                points = routeLatLngs,
                color = MainPurple,
                width = 15f,
            )
        }

        successLatLng?.let { latLng ->
            Marker(
                state = MarkerState(position = latLng),
                title = "QUEST",
            )
        }
    }
}

@Composable
private fun ResultQuestImageSection(imageUrl: String) {
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
    )
}

@Composable
private fun ResultOtherImagesSection(images: List<String>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        images.forEach { imageUrl ->
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = "Other User Image",
                modifier = Modifier
                    .weight(0.23f)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray),
                    )
                },
            )
        }
        repeat(4 - images.size) {
            Spacer(modifier = Modifier.weight(0.23f))
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
