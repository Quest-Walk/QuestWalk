package com.hapataka.questwalk.feature.quest

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hapataka.questwalk.core.ui.LocalPaddingValues
import com.hapataka.questwalk.core.ui.UiState
import kotlinx.coroutines.flow.collectLatest

private val Purple = Color(0xFF6B4EFF)
private val LightGray = Color(0xFFF5F5F5)
private val TextGray = Color(0xFFC8C8C8)

@Composable
fun QuestRoute(
    padding: PaddingValues = LocalPaddingValues.current,
    viewModel: QuestViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onQuestDetailClick: (String) -> Unit = {},
    onQuestSelected: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var selectedKeyword by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.event.collectLatest { event ->
            when (event) {
                QuestEvent.QuestSelected -> onQuestSelected()
                is QuestEvent.ShowQuestDetail -> onQuestDetailClick(event.keyword)
                is QuestEvent.Error -> Unit
            }
        }
    }

    selectedKeyword?.let { keyword ->
        val isAlreadySuccess = (uiState as? UiState.Success)?.data?.successKeywords?.contains(keyword) == true

        QuestConfirmDialog(
            keyword = keyword,
            isAlreadySuccess = isAlreadySuccess,
            onConfirm = {
                if (!isAlreadySuccess) {
                    viewModel.onIntent(QuestIntent.SelectQuest(keyword))
                }
                selectedKeyword = null
            },
            onDismiss = { selectedKeyword = null }
        )
    }

    QuestScreen(
        uiState = uiState,
        padding = padding,
        onBackClick = onBackClick,
        onFilterLevel = { viewModel.onIntent(QuestIntent.FilterLevel(it)) },
        onQuestClick = { selectedKeyword = it },
        onQuestDetailClick = { viewModel.onIntent(QuestIntent.ShowQuestDetail(it)) },
    )
}

@Composable
private fun QuestScreen(
    uiState: UiState<QuestUiState>,
    padding: PaddingValues,
    onBackClick: () -> Unit,
    onFilterLevel: (Int) -> Unit,
    onQuestClick: (String) -> Unit,
    onQuestDetailClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(padding)
    ) {
        TopBar(onBackClick = onBackClick)

        when (uiState) {
            is UiState.Loading -> LoadingContent()
            is UiState.Success -> QuestContent(
                state = uiState.data,
                onFilterLevel = onFilterLevel,
                onQuestClick = onQuestClick,
                onQuestDetailClick = onQuestDetailClick,
            )
            is UiState.Failure -> ErrorContent(error = uiState.error)
            is UiState.Idle -> Unit
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
            Text(text = "<", style = MaterialTheme.typography.titleLarge)
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
            text = error.message ?: "오류가 발생했습니다",
            color = Color.Red
        )
    }
}

@Composable
private fun QuestContent(
    state: QuestUiState,
    onFilterLevel: (Int) -> Unit,
    onQuestClick: (String) -> Unit,
    onQuestDetailClick: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LevelTabs(
            selectedLevel = state.selectedLevel,
            onLevelSelected = onFilterLevel
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.quests, key = { it.keyword }) { quest ->
                QuestItem(
                    quest = quest,
                    onClick = { onQuestClick(quest.keyword) },
                    onMoreClick = { onQuestDetailClick(quest.keyword) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun LevelTabs(
    selectedLevel: Int,
    onLevelSelected: (Int) -> Unit,
) {
    val tabs = listOf("전체" to 0, "Lv.1" to 1, "Lv.2" to 2, "Lv.3" to 3)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEach { (label, level) ->
            val isSelected = selectedLevel == level
            Text(
                text = label,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) Purple else LightGray)
                    .clickable { onLevelSelected(level) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color = if (isSelected) Color.White else Color.Black,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun QuestItem(
    quest: QuestItemUiModel,
    onClick: () -> Unit,
    onMoreClick: () -> Unit,
) {
    val backgroundColor = if (quest.isSuccess) LightGray else Color.White
    val textColor = if (quest.isSuccess) TextGray else Purple
    val progressColor = if (quest.isSuccess) TextGray else Purple

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Level Badge
        LevelBadge(level = quest.level, isSuccess = quest.isSuccess)

        Spacer(modifier = Modifier.width(12.dp))

        // Quest Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = quest.keyword,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.LightGray.copy(alpha = 0.3f))
            ) {
                LinearProgressIndicator(
                    progress = { (quest.completeRate / 100f).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = progressColor,
                    trackColor = Color.Transparent,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${quest.completeRate}% 달성",
                color = textColor,
                fontSize = 12.sp
            )
        }

        // More Button
        if (quest.successCount > 0) {
            TextButton(onClick = onMoreClick) {
                Text(text = "더보기", color = Purple)
            }
        }
    }
}

@Composable
private fun LevelBadge(level: Int, isSuccess: Boolean) {
    val text = "Lv.$level"
    val backgroundColor = if (isSuccess) TextGray else Purple

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun QuestConfirmDialog(
    keyword: String,
    isAlreadySuccess: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = keyword,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = if (isAlreadySuccess) {
                    "이미 완료한 키워드입니다.\n다른 키워드를 선택해주세요."
                } else {
                    "이 퀘스트를 선택하시겠습니까?"
                },
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Purple)
            ) {
                Text(if (isAlreadySuccess) "확인" else "선택")
            }
        },
        dismissButton = {
            if (!isAlreadySuccess) {
                TextButton(onClick = onDismiss) {
                    Text("취소")
                }
            }
        }
    )
}
