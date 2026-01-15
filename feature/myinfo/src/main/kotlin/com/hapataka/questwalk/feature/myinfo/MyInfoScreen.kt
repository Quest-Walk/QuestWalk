package com.hapataka.questwalk.feature.myinfo

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hapataka.questwalk.core.designsystem.component.PixelPurpleButton
import com.hapataka.questwalk.core.designsystem.component.QuestWalkTopAppBar
import com.hapataka.questwalk.core.designsystem.theme.Black
import com.hapataka.questwalk.core.designsystem.theme.MainPurple
import com.hapataka.questwalk.core.designsystem.theme.Typography
import com.hapataka.questwalk.core.ui.LocalPaddingValues
import com.hapataka.questwalk.core.ui.UiState
import com.hapataka.questwalk.core.ui.component.Character

private val DangerRed = Color(0xFFFF0064)

@Composable
fun MyInfoRoute(
    padding: PaddingValues = LocalPaddingValues.current,
    viewModel: MyInfoViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onLogoutSuccess: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val logoutEvent by viewModel.logoutEvent.collectAsStateWithLifecycle()

    LaunchedEffect(logoutEvent) {
        if (logoutEvent) {
            onLogoutSuccess()
        }
    }

    MyInfoScreen(
        uiState = uiState,
        padding = padding,
        onBackClick = onBackClick,
        onLogoutClick = viewModel::logout,
        onWithdrawClick = { /* TODO */ },
    )
}

@Composable
private fun MyInfoScreen(
    uiState: UiState<MyInfoUiState>,
    padding: PaddingValues,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onWithdrawClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(padding)
    ) {
        QuestWalkTopAppBar(
            title = "내 정보",
            contentColor = MainPurple,
            leadingIcon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            onClickLeadingIcon = onBackClick,
        )

        when (uiState) {
            is UiState.Loading -> LoadingContent()
            is UiState.Success -> MyInfoContent(
                state = uiState.data,
                onLogoutClick = onLogoutClick,
                onWithdrawClick = onWithdrawClick,
            )
            is UiState.Failure -> ErrorContent()
            is UiState.Idle -> Unit
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MainPurple)
    }
}

@Composable
private fun ErrorContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "정보를 불러올 수 없습니다",
            style = Typography.bodyMedium,
            color = DangerRed
        )
    }
}

@Composable
private fun MyInfoContent(
    state: MyInfoUiState,
    onLogoutClick: () -> Unit,
    onWithdrawClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Character Image
        Box(
            modifier = Modifier
                .width(206.dp)
                .height(220.dp),
            contentAlignment = Alignment.Center
        ) {
            Character(
                character = com.hapataka.questwalk.core.ui.component.Character.BEAR,
                isAnimate = false,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Nickname with edit icon
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = state.userName.ifEmpty { "닉네임 입력창" },
                style = Typography.bodyMedium,
                color = Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Edit nickname",
                modifier = Modifier.size(20.dp),
                tint = MainPurple
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Info Items
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            InfoItem(
                label = "모험한 시간",
                value = state.totalTimeFormatted
            )

            InfoItem(
                label = "모험한 거리",
                value = state.totalDistanceFormatted
            )

            // Step with Kcal
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "총 걸음수",
                    style = Typography.titleSmall,
                    color = MainPurple
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = state.totalStepFormatted,
                        style = Typography.bodyLarge,
                        color = Black
                    )
                    Text(
                        text = state.totalKcalFormatted,
                        style = Typography.labelMedium,
                        color = Black
                    )
                }
            }

            // Quest & Achievement Row
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "해결한 퀘스트",
                        style = Typography.titleSmall,
                        color = MainPurple
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${state.questCount}개",
                        style = Typography.bodyLarge,
                        color = Black
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "달성한 업적",
                        style = Typography.titleSmall,
                        color = MainPurple
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${state.achievementCount}개",
                        style = Typography.bodyLarge,
                        color = Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Logout Button (Pixel style)
        PixelPurpleButton(
            onClick = onLogoutClick,
            text = "로그아웃",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 21.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Withdraw text
        Text(
            text = "탈퇴하기",
            style = Typography.bodyMedium,
            color = DangerRed,
            modifier = Modifier
                .clickable(onClick = onWithdrawClick)
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun InfoItem(
    label: String,
    value: String,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = Typography.titleSmall,
            color = MainPurple
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = Typography.bodyLarge,
            color = Black
        )
    }
}
