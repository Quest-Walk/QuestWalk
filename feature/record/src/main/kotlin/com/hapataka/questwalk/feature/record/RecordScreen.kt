package com.hapataka.questwalk.feature.record

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.SubcomposeAsyncImage
import com.hapataka.questwalk.core.model.History
import com.hapataka.questwalk.core.ui.LocalPaddingValues
import com.hapataka.questwalk.core.ui.UiState
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RecordRoute(
    padding: PaddingValues = LocalPaddingValues.current,
    viewModel: RecordViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onHistoryClick: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is RecordEvent.NavigateToResult -> onHistoryClick(event.historyId)
            }
        }
    }

    RecordScreen(
        uiState = uiState,
        padding = padding,
        onBackClick = onBackClick,
        onIntent = viewModel::onIntent,
    )
}

@Composable
private fun RecordScreen(
    uiState: UiState<RecordUiState>,
    padding: PaddingValues,
    onBackClick: () -> Unit = {},
    onIntent: (RecordIntent) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        TopBar(onBackClick = onBackClick)
        when (uiState) {
            is UiState.Loading -> LoadingContent()
            is UiState.Success -> RecordContent(
                histories = uiState.data.histories,
                achieveItems = uiState.data.achieveItems,
                onHistoryClick = { onIntent(RecordIntent.ClickHistory(it)) },
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
            .height(56.dp)
            .background(Color.White),
        contentAlignment = Alignment.CenterStart
    ) {
        BackButton(onClick = onBackClick)
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RecordContent(
    histories: List<History>,
    achieveItems: List<AchieveItemUiModel>,
    onHistoryClick: (String) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { 2 })

    HorizontalPager(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        state = pagerState
    ) { page ->
        when (page) {
            0 -> HistoryScreen(histories, onHistoryClick)
            1 -> AchievementScreen(achieveItems)
        }
    }
}

@Composable
private fun HistoryScreen(
    histories: List<History>,
    onHistoryClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        Header(title = "히스토리")
        HistoryGridList(histories, onHistoryClick)
    }
}

@Composable
private fun Header(title: String) {
    Text(
        text = title,
        style = typography.titleLarge,
        modifier = Modifier
            .padding(start = 20.dp, bottom = 16.dp)
    )
}

@Composable
private fun HistoryGridList(
    histories: List<History>,
    onHistoryClick: (String) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        items(histories, key = { it.id }) { history ->
            HistoryItem(
                item = history,
                onClick = { onHistoryClick(history.id) }
            )
        }
    }
}

@Composable
private fun HistoryItem(
    item: History,
    onClick: () -> Unit,
) {
    SubcomposeAsyncImage(
        modifier = Modifier
            .height(92.dp)
            .width(92.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        model = when (item) {
            is History.QuestResult -> item.imageUrl
            is History.Achievement -> null
        },
        contentDescription = null,
        contentScale = ContentScale.FillHeight,
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }
        }
    )
}

@Composable
private fun AchievementScreen(achieveItems: List<AchieveItemUiModel>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        Header(title = "업적")
        AchievementGridList(achieveItems)
    }
}

@Composable
private fun AchievementGridList(achieveItems: List<AchieveItemUiModel>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        items(achieveItems, key = { it.achieveId }) { item ->
            AchievementItem(item = item)
        }
    }
}

@Composable
private fun AchievementItem(item: AchieveItemUiModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SubcomposeAsyncImage(
            modifier = Modifier
                .height(92.dp)
                .width(92.dp)
                .clip(RoundedCornerShape(12.dp)),
            model = item.achieveIcon,
            contentDescription = item.achieveTitle,
            contentScale = ContentScale.FillHeight,
            alpha = if (item.isAchieved) 1f else 0.3f,
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        )
        Text(
            text = item.achieveTitle,
            style = typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(56.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // TODO: Replace with design system icon
        Text(text = "<", style = typography.titleLarge)
    }
}
