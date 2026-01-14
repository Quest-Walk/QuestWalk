package com.hapataka.questwalk.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.updateBounds
import com.hapataka.questwalk.core.designsystem.component.ImageButton
import com.hapataka.questwalk.core.designsystem.component.PixelChipButton
import com.hapataka.questwalk.core.designsystem.theme.Black
import com.hapataka.questwalk.core.designsystem.theme.SystemCyan
import com.hapataka.questwalk.core.designsystem.theme.SystemGray
import com.hapataka.questwalk.core.designsystem.theme.SystemLemon
import com.hapataka.questwalk.core.designsystem.theme.Typography
import com.hapataka.questwalk.core.ui.LocalPaddingValues
import com.hapataka.questwalk.core.ui.component.Character
import com.hapataka.questwalk.core.ui.component.HorizontalScrollingBackground
import com.hapataka.questwalk.feature.home.R as HomeR

const val QUEST_STOP = 0
const val QUEST_START = 1
const val QUEST_SUCCESS = 2

@Composable
fun HomeRoute(
    padding: PaddingValues = LocalPaddingValues.current,
    uiState: HomeUiState,
    onStartClick: () -> Unit = {},
    onStopClick: () -> Unit = {},
    onCameraClick: () -> Unit = {},
    onCompleteClick: () -> Unit = {},
    onQuestChangeClick: () -> Unit = {},
    onWeatherClick: () -> Unit = {},
    onMyInfoClick: () -> Unit = {},
    onRecordClick: () -> Unit = {},
) {
    HomeScreen(
        uiState = uiState,
        padding = padding,
        onStartClick = onStartClick,
        onStopClick = onStopClick,
        onCameraClick = onCameraClick,
        onCompleteClick = onCompleteClick,
        onQuestChangeClick = onQuestChangeClick,
        onWeatherClick = onWeatherClick,
        onMyInfoClick = onMyInfoClick,
        onRecordClick = onRecordClick,
    )
}

@Composable
private fun HomeScreen(
    uiState: HomeUiState,
    padding: PaddingValues,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onCameraClick: () -> Unit,
    onCompleteClick: () -> Unit,
    onQuestChangeClick: () -> Unit,
    onWeatherClick: () -> Unit,
    onMyInfoClick: () -> Unit,
    onRecordClick: () -> Unit,
) {
    var animState by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.playState) {
        animState = uiState.playState != QUEST_STOP
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = SystemGray)
            .padding(padding)
    ) {
        // Background and Character Section
        Box(
            modifier = Modifier
                .fillMaxHeight(0.7f)
                .fillMaxWidth(),
        ) {
            BackgroundLayers(
                isAnimate = animState,
                currentTime = uiState.currentTime
            )

            Character(
                character = Character.BEAR,
                isAnimate = animState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxHeight(0.42f)
                    .padding(bottom = 72.dp)
            )

            // Side Buttons
            SideButtons(
                statusBarHeight = uiState.statusBarHeight,
                onWeatherClick = onWeatherClick,
                onMyInfoClick = onMyInfoClick,
                onRecordClick = onRecordClick,
            )

            // Quest Info (Keyword, Level, Change Button)
            QuestInfoOverlay(
                keyword = uiState.currentKeyword,
                level = uiState.keywordLevel,
                playState = uiState.playState,
                statusBarHeight = uiState.statusBarHeight,
                onQuestChangeClick = onQuestChangeClick,
            )
        }

        // Info Bar
        InfoBar(
            playState = uiState.playState,
            duration = uiState.duration,
            step = uiState.step,
            distance = uiState.distance,
        )

        // Control Buttons
        ControlButtons(
            playState = uiState.playState,
            onStartClick = onStartClick,
            onStopClick = onStopClick,
            onCameraClick = onCameraClick,
            onCompleteClick = onCompleteClick,
        )
    }
}

@Composable
private fun BackgroundLayers(
    isAnimate: Boolean,
    currentTime: Int,
) {
    val isDayTime = currentTime in 7..18

    HorizontalScrollingBackground(
        isAnimate = isAnimate,
        modifier = Modifier.fillMaxSize(),
        imgId = if (isDayTime) {
            com.hapataka.questwalk.core.ui.R.drawable.bg_landscape_day
        } else {
            com.hapataka.questwalk.core.ui.R.drawable.bg_landscape_night
        },
        duration = 160000
    )

    HorizontalScrollingBackground(
        isAnimate = isAnimate,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
            .padding(top = 80.dp),
        imgId = if (isDayTime) {
            HomeR.drawable.background_day_layer2
        } else {
            com.hapataka.questwalk.core.ui.R.drawable.bg_starts
        },
        duration = 40000
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        HorizontalScrollingBackground(
            isAnimate = isAnimate,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f),
            imgId = com.hapataka.questwalk.core.ui.R.drawable.bg_ground,
            duration = 15000
        )
    }
}

@Composable
private fun SideButtons(
    statusBarHeight: Dp,
    onWeatherClick: () -> Unit,
    onMyInfoClick: () -> Unit,
    onRecordClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 16.dp + statusBarHeight, end = 12.dp)
                .width(64.dp)
                .align(Alignment.TopEnd),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(-2.dp),
        ) {
            ImageButton(
                releasedPainterResource = painterResource(HomeR.drawable.btn_weather),
                contentDescription = "날씨 버튼",
                modifier = Modifier.size(64.dp),
                onClick = onWeatherClick
            )
            Image(
                painter = painterResource(HomeR.drawable.image_chain),
                contentDescription = null,
            )
            ImageButton(
                releasedPainterResource = painterResource(HomeR.drawable.btn_my_info),
                contentDescription = "내 정보 버튼",
                modifier = Modifier.size(52.dp),
                onClick = onMyInfoClick
            )
            Image(
                painter = painterResource(HomeR.drawable.image_chain),
                contentDescription = null,
            )
            ImageButton(
                releasedPainterResource = painterResource(HomeR.drawable.btn_history),
                contentDescription = "히스토리 버튼",
                modifier = Modifier.size(52.dp),
                onClick = onRecordClick
            )
        }
    }
}

@Composable
private fun QuestInfoOverlay(
    keyword: String,
    level: Int,
    playState: Int,
    statusBarHeight: Dp,
    onQuestChangeClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter)
                .padding(top = 100.dp + statusBarHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
        ) {
            // Level stars
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
            ) {
                repeat(level) {
                    Image(
                        painter = painterResource(HomeR.drawable.ic_level_star),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            // Keyword with stroke
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = keyword,
                    style = Typography.displayMedium.copy(
                        drawStyle = Stroke(
                            width = 16f,
                            miter = 16f,
                            cap = StrokeCap.Round,
                        )
                    ),
                    color = Black,
                )
                Text(
                    text = keyword,
                    style = Typography.displayMedium,
                    color = Color.White,
                )
            }

            // Quest change button (only when stopped)
            if (playState == QUEST_STOP) {
                PixelChipButton(
                    text = "퀘스트 변경",
                    onClick = onQuestChangeClick
                )
            }
        }
    }
}

@Composable
private fun InfoBar(
    playState: Int,
    duration: String,
    step: String,
    distance: String,
) {
    val context = LocalContext.current
    val bg = remember {
        ContextCompat.getDrawable(context, HomeR.drawable.bg_info_bar)
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            modifier = Modifier.fillMaxWidth(),
            painter = painterResource(HomeR.drawable.image_gradiant_dividor),
            contentDescription = null,
            contentScale = ContentScale.FillHeight
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(56.dp)
                .drawBehind {
                    bg?.updateBounds(0, 0, size.width.toInt(), size.height.toInt())
                    bg?.draw(drawContext.canvas.nativeCanvas)
                }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            if (playState == QUEST_STOP) {
                Text(
                    text = "시작하려면, START버튼을 눌러주세요",
                    style = Typography.bodyMedium,
                    color = SystemGray
                )
            } else {
                Text(
                    text = duration,
                    style = Typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    text = "$step 걸음",
                    style = Typography.bodyMedium,
                    color = SystemCyan
                )
                Text(
                    text = distance,
                    style = Typography.bodyMedium,
                    color = SystemLemon
                )
            }
        }
    }
}

@Composable
private fun ControlButtons(
    playState: Int,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onCameraClick: () -> Unit,
    onCompleteClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (playState) {
            QUEST_STOP -> {
                StartButton(
                    modifier = Modifier.fillMaxWidth(0.35f),
                    onClick = onStartClick
                )
            }

            QUEST_START -> {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ImageButton(
                        releasedPainterResource = painterResource(HomeR.drawable.btn_stop_fail),
                        contentDescription = "퀘스트 포기 버튼",
                        modifier = Modifier
                            .fillMaxHeight(0.65f)
                            .aspectRatio(1f),
                        onClick = onStopClick
                    )
                    ImageButton(
                        releasedPainterResource = painterResource(HomeR.drawable.btn_camera),
                        contentDescription = "촬영 하기 버튼",
                        modifier = Modifier
                            .fillMaxHeight(0.65f)
                            .aspectRatio(1f),
                        onClick = onCameraClick
                    )
                }
            }

            QUEST_SUCCESS -> {
                ImageButton(
                    releasedPainterResource = painterResource(HomeR.drawable.btn_stop_success),
                    contentDescription = "완료하기 버튼",
                    modifier = Modifier
                        .fillMaxHeight(0.65f)
                        .aspectRatio(2f),
                    onClick = onCompleteClick,
                )
            }
        }
    }
}

@Composable
private fun StartButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val buttonBackground = painterResource(
        if (isPressed) HomeR.drawable.btn_start_press else HomeR.drawable.btn_start_defualt
    )

    Box(
        modifier = modifier.clickable(
            onClick = onClick,
            indication = null,
            interactionSource = interactionSource
        ),
    ) {
        Image(
            painter = buttonBackground,
            contentDescription = "퀘스트 시작 버튼",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

data class HomeUiState(
    val currentKeyword: String = "",
    val keywordLevel: Int = 0,
    val playState: Int = QUEST_STOP,
    val currentTime: Int = 12,
    val duration: String = "",
    val step: String = "0",
    val distance: String = "0m",
    val statusBarHeight: Dp = 0.dp,
    val navigationBarHeight: Dp = 0.dp,
)
