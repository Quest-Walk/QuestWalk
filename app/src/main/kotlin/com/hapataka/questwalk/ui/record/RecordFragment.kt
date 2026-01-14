package com.hapataka.questwalk.ui.record

import android.os.Bundle
import android.util.Log
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
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import coil3.compose.AsyncImage
import com.hapataka.questwalk.R
import com.hapataka.questwalk.core.designsystem.theme.QuestWalkTheme
import com.hapataka.questwalk.core.model.History
import com.hapataka.questwalk.util.TAG
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecordFragment : Fragment() {
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private val viewModel by viewModels<RecordViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return ComposeView(requireActivity()).apply {
            requireActivity().enableEdgeToEdge()
            setContent {
                QuestWalkTheme(lightBar = true) {
                    Scaffold(modifier = Modifier.background(Color.White)) { paddingValues ->
                        RecordRoute(paddingValues)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getHistories()
    }

    @Composable
    fun RecordRoute(paddingValues: PaddingValues = PaddingValues()) {
        val histories by viewModel.histories.observeAsState()

        RecordScreen(
            paddingValues = paddingValues,
            histories = histories,
        )
    }

    @Composable
    fun RecordScreen(
        paddingValues: PaddingValues = PaddingValues(),
        histories: List<History>? = emptyList(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TopBar()
            RecordContent(histories)
        }
    }

    @Composable
    fun TopBar() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color.White),
            contentAlignment = Alignment.CenterStart
        ) {
            BackButton()
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun RecordContent(histories: List<History>?) {
        val pagerState = rememberPagerState(pageCount = { 2 })

        HorizontalPager(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            state = pagerState
        ) { page ->
            when (page) {
                0 -> histories?.let { HistoryScreen(it) }
                1 -> AchievementScreen()
            }
        }
    }

    @Composable
    fun HistoryScreen(histories: List<History>) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Header(title = "히스토리")
            GridList(histories)
        }
    }

    @Composable
    fun Header(title: String) {
        Text(
            text = title,
            style = typography.titleLarge,
            modifier = Modifier
                .padding(start = 20.dp, bottom = 16.dp)
        )
    }

    @Composable
    fun GridList(histories: List<History>) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            items(count = histories.size) { ContentItem(item = histories[it]) }
        }
    }

    @Composable
    fun ContentItem(item: History) {
        AsyncImage(
            modifier = Modifier
                .height(92.dp)
                .width(92.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { Log.d(TAG, "ContentItem: $item") },
            model = when (item) {
                is History.QuestResult -> {
                    item.imageUrl ?: R.drawable.image_empty
                }

                is History.Achievement -> {
//                    item.iconUrl
                }
            },
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
        )
    }

    @Composable
    fun AchievementScreen() {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Header(title = "업적")
        }
    }

    @Composable
    fun BackButton() {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(56.dp)
                .clickable {
                    navController.popBackStack()
                },
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