package com.hapataka.questwalk.feature.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hapataka.questwalk.core.ui.LocalPaddingValues
import com.hapataka.questwalk.core.ui.UiState

private val Purple = Color(0xFF6B4EFF)
private val LightPurple = Color(0xFFB8A9FF)
private val GrayText = Color(0xFFC8C8C8)

@Composable
fun WeatherRoute(
    padding: PaddingValues = LocalPaddingValues.current,
    viewModel: WeatherViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WeatherScreen(
        uiState = uiState,
        padding = padding,
        onBackClick = onBackClick,
    )
}

@Composable
private fun WeatherScreen(
    uiState: UiState<WeatherUiState>,
    padding: PaddingValues,
    onBackClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(LightPurple, Color.White),
                    startY = 0f,
                    endY = 1000f
                )
            )
            .padding(padding)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(onBackClick = onBackClick)

            when (uiState) {
                is UiState.Loading -> LoadingContent()
                is UiState.Success -> WeatherContent(state = uiState.data)
                is UiState.Failure -> ErrorContent(error = uiState.error)
                is UiState.Idle -> Unit
            }
        }
    }
}

@Composable
private fun TopBar(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(56.dp)
                .clickable(onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "<",
                style = MaterialTheme.typography.titleLarge,
                color = Purple
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Purple)
    }
}

@Composable
private fun ErrorContent(error: Throwable) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "잠시후 다시 시도해주세요!",
            color = Color.Red
        )
    }
}

@Composable
private fun WeatherContent(state: WeatherUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        // Weather Preview Message
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.9f))
                .padding(horizontal = 12.dp, vertical = 24.dp)
        ) {
            Text(
                text = buildPreviewMessage(state.preview),
                fontSize = 14.sp,
                lineHeight = 22.sp,
                color = Color.DarkGray
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Weather Icon placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🧙",
                fontSize = 80.sp
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Arrow down
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "▼",
                fontSize = 24.sp,
                color = Purple
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Dust Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "미세먼지",
                    fontSize = 14.sp,
                    color = GrayText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = state.dust.pm10Value,
                    fontSize = 14.sp,
                    color = GrayText
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "초미세먼지",
                    fontSize = 14.sp,
                    color = GrayText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = state.dust.pm25Value,
                    fontSize = 14.sp,
                    color = GrayText
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Hourly Weather
        Text(
            text = "시간대별 날씨",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(state.weatherItems, key = { it.time }) { item ->
                WeatherItemCard(item = item)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun WeatherItemCard(item: WeatherItemUiModel) {
    Column(
        modifier = Modifier
            .width(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.8f))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = formatTime(item.time),
            fontSize = 12.sp,
            color = GrayText
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = getWeatherEmoji(item.sky, item.precipType),
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${item.temp}°",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )
    }
}

private fun buildPreviewMessage(preview: WeatherPreviewUiModel): String {
    val precipMessage = if (preview.precipState.isNotEmpty()) " ${preview.precipState}" else ""
    return "현재 온도는 ${preview.currentTemp}도 이고, 하늘 상태는 ${preview.skyState}$precipMessage " +
            "미세먼지 상태는 ${preview.miseState} 초미세 먼지 상태는 ${preview.choMiseState} 오늘 여행에 참고하라구!!"
}

private fun formatTime(time: String): String {
    return if (time.length >= 2) {
        "${time.substring(0, 2)}시"
    } else {
        time
    }
}

private fun getWeatherEmoji(sky: String, precipType: String): String {
    val precip = precipType.toIntOrNull() ?: 0
    if (precip > 0) {
        return when (precip) {
            1, 4 -> "🌧️"
            2 -> "🌨️"
            3 -> "❄️"
            else -> "🌧️"
        }
    }

    val skyValue = sky.toIntOrNull() ?: 0
    return when {
        skyValue <= 5 -> "☀️"
        skyValue <= 8 -> "⛅"
        else -> "☁️"
    }
}
