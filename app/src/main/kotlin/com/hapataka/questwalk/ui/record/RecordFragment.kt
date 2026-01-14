package com.hapataka.questwalk.ui.record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import coil3.compose.AsyncImage
import com.hapataka.questwalk.R
import com.hapataka.questwalk.core.designsystem.theme.QuestWalkTheme
import com.hapataka.questwalk.core.model.History
import com.hapataka.questwalk.core.ui.UiState
import com.hapataka.questwalk.ui.record.model.RecordItem.AchieveItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecordFragment : Fragment() {
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private val viewModel by viewModels<RecordViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireActivity()).apply {
            requireActivity().enableEdgeToEdge()
            setContent {
                QuestWalkTheme(lightBar = true) {
                    Scaffold(modifier = Modifier.background(Color.White)) { paddingValues ->
                        RecordRoute(
                            paddingValues = paddingValues,
                            onBackClick = { navController.popBackStack() },
                            onHistoryClick = { historyId ->
                                viewModel.onAction(RecordAction.ClickHistory(historyId))
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun RecordRoute(
        paddingValues: PaddingValues = PaddingValues(),
        onBackClick: () -> Unit = {},
        onHistoryClick: (String) -> Unit = {},
    ) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        RecordScreen(
            uiState = uiState,
            paddingValues = paddingValues,
            onBackClick = onBackClick,
            onHistoryClick = onHistoryClick,
        )
    }

    @Composable
    private fun RecordScreen(
        uiState: UiState<RecordUiState>,
        paddingValues: PaddingValues = PaddingValues(),
        onBackClick: () -> Unit = {},
        onHistoryClick: (String) -> Unit = {},
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TopBar(onBackClick = onBackClick)
            when (uiState) {
                is UiState.Loading -> LoadingContent()
                is UiState.Success -> RecordContent(
                    histories = uiState.data.histories,
                    achieveItems = uiState.data.achieveItems,
                    onHistoryClick = onHistoryClick,
                )
                is UiState.Failure -> ErrorContent(error = uiState.error)
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

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun RecordContent(
        histories: List<History>,
        achieveItems: List<AchieveItem>,
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
            items(count = histories.size) { index ->
                HistoryItem(
                    item = histories[index],
                    onClick = { onHistoryClick(histories[index].id) }
                )
            }
        }
    }

    @Composable
    private fun HistoryItem(
        item: History,
        onClick: () -> Unit,
    ) {
        AsyncImage(
            modifier = Modifier
                .height(92.dp)
                .width(92.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onClick),
            model = when (item) {
                is History.QuestResult -> item.imageUrl ?: R.drawable.image_empty
                is History.Achievement -> R.drawable.image_empty
            },
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
        )
    }

    @Composable
    private fun AchievementScreen(achieveItems: List<AchieveItem>) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Header(title = "업적")
            AchievementGridList(achieveItems)
        }
    }

    @Composable
    private fun AchievementGridList(achieveItems: List<AchieveItem>) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            items(count = achieveItems.size) { index ->
                AchievementItem(item = achieveItems[index])
            }
        }
    }

    @Composable
    private fun AchievementItem(item: AchieveItem) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                modifier = Modifier
                    .height(92.dp)
                    .width(92.dp)
                    .clip(RoundedCornerShape(12.dp)),
                model = item.achieveIcon,
                contentDescription = item.achieveTitle,
                contentScale = ContentScale.FillHeight,
                alpha = if (item.isAchieved) 1f else 0.3f,
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
            AsyncImage(
                modifier = Modifier
                    .height(20.dp)
                    .width(20.dp),
                model = R.drawable.btn_back,
                contentDescription = null,
                contentScale = ContentScale.FillHeight
            )
        }
    }
}