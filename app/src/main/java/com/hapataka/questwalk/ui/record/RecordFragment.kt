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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import coil.imageLoader
import com.hapataka.questwalk.R
import com.hapataka.questwalk.data.model.HistoryModel
import com.hapataka.questwalk.databinding.FragmentRecordBinding
import com.hapataka.questwalk.ui.common.BaseFragment
import com.hapataka.questwalk.ui.record.adapter.RecordItemAdapter
import com.hapataka.questwalk.ui.theme.Quest_Walk_Theme
import com.hapataka.questwalk.util.TAG
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecordFragment : BaseFragment<FragmentRecordBinding>(FragmentRecordBinding::inflate) {
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private val viewModel by viewModels<RecordViewModel>()
    private lateinit var recordItemAdapter: RecordItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireActivity()).apply {
            requireActivity().enableEdgeToEdge()
            setContent {
                Quest_Walk_Theme {
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
        histories: List<HistoryModel>? = emptyList(),
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
    fun RecordContent(histories: List<HistoryModel>?) {
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
    fun HistoryScreen(histories: List<HistoryModel>) {
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
    fun GridList(histories: List<HistoryModel>) {
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
    fun ContentItem(item: HistoryModel) {
        AsyncImage(
            modifier = Modifier
                .height(92.dp)
                .width(92.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { Log.d(TAG, "ContentItem: $item") },
            model = when (item) {
                is HistoryModel.ResultRecordModel -> {
                    item.questImg ?: R.drawable.image_empty
                }

                is HistoryModel.AchievementRecordModel -> {
                    item.iconUrl
                }
            },
            contentDescription = null,
            imageLoader = requireContext().imageLoader,
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
//            GridList(histories =)
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
                imageLoader = requireContext().imageLoader,
                contentScale = ContentScale.FillHeight
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun RecordScreenPreview() {
        Quest_Walk_Theme {
            RecordScreen()
        }
    }


//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        initView()
//        setObserver()
//        getItems()
//    }
//
//    private fun initView() {
//        initBackButton()
//        recordItemAdapter = RecordItemAdapter(requireActivity())
//        binding.innerContainer.setPadding()
//        requireActivity().setLightBarColor(true)
//    }
//
//    private fun setObserver() {
//        with(viewModel) {
//            recordItems.observe(viewLifecycleOwner) { items ->
//                if (items != recordItemAdapter.items) {
//                    initViewPager(items)
//                }
//            }
//            achieveItems.observe(viewLifecycleOwner) {
//                recordItemAdapter.achieveItems = it
//            }
//        }
//    }
//
//    private fun getItems() {
//        viewModel.getRecordItems()
//    }
//
//    private fun initViewPager(itemList: List<RecordItem>) {
//        recordItemAdapter.items = itemList
//
//        val tabTitle = listOf("히스토리", "업적")
//
//        with(binding) {
//            vpRecordContents.apply {
//                adapter = recordItemAdapter
//                isSaveEnabled = false
//            }
//            TabLayoutMediator(tlRecordMenu, vpRecordContents) { tab, position ->
//                tab.text = tabTitle[position]
//            }.attach()
//        }
//    }

    private fun initBackButton() {
        binding.btnBack.setOnClickListener {
            navController.popBackStack()
        }
    }
}