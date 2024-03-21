package com.hapataka.questwalk.ui.record

import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentRecordItemBinding
import com.hapataka.questwalk.ui.mainactivity.MainViewModel
import com.hapataka.questwalk.ui.record.adapter.HEADER_TYPE
import com.hapataka.questwalk.ui.record.adapter.RecordDetailAdapter
import com.hapataka.questwalk.ui.record.model.RecordItem
import com.hapataka.questwalk.ui.result.QUEST_KEYWORD
import com.hapataka.questwalk.ui.result.REGISTER_TIME
import com.hapataka.questwalk.ui.result.USER_ID
import com.hapataka.questwalk.util.BaseFragment
import com.hapataka.questwalk.util.ViewModelFactory

class RecordItemFragment(private val items: List<RecordItem>) :
    BaseFragment<FragmentRecordItemBinding>(FragmentRecordItemBinding::inflate) {
    private val recordDetailAdapter by lazy { RecordDetailAdapter(requireContext()) }
    private val gridLayoutManager by lazy { GridLayoutManager(requireContext(), 3) }
    private val navController by lazy { (parentFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment).findNavController() }
    private val mainViewModel by activityViewModels<MainViewModel> { ViewModelFactory(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initItemClick()
    }

    private fun initRecyclerView() {
        with(binding.rvRecordDetail) {
            adapter = recordDetailAdapter
            setSpanSizeLookup()
            layoutManager = gridLayoutManager
            if (itemDecorationCount < 1) {
                addItemDecoration(itemDeco)
            }
        }
        recordDetailAdapter.submitList(items)
    }

    private fun setSpanSizeLookup() {
        gridLayoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = binding.rvRecordDetail.adapter?.getItemViewType(position)

                return when (viewType) {
                    HEADER_TYPE -> gridLayoutManager.spanCount
                    else -> 1
                }
            }
        }
    }

    private fun initItemClick() {
        object : RecordDetailAdapter.ItemClick {
            override fun onClick(item: RecordItem) {
                if (item is RecordItem.AchieveItem) {
                    val dialog = AchieveDialog(item)

                    dialog.show(parentFragmentManager, "AchieveDialog")
                    return
                }

                if (item is RecordItem.ResultItem) {
                    mainViewModel.moveToResult { uid, _ ->
                        val bundle = Bundle().apply {
                            putString(USER_ID, uid)
                            putString(QUEST_KEYWORD, item.keyword)
                            putString(REGISTER_TIME, item.registerAt)
                        }
                        navController.navigate(R.id.action_frag_record_to_frag_result, bundle)
                    }
                }
            }
        }.also { recordDetailAdapter.itemClick = it }
    }

    private val itemDeco = object : ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            val viewParam = view.layoutParams as GridLayoutManager.LayoutParams

            if (viewParam.spanSize == 1) {
                outRect.bottom = 20.dpToPx()
            }

            when (viewParam.spanIndex) {
                0 -> {
                    outRect.right = 8.dpToPx()
                }

                1 -> {
                    outRect.right = 8.dpToPx()
                    outRect.left = 8.dpToPx()
                }

                2 -> {
                    outRect.left = 8.dpToPx()
                }
            }
        }
    }

    fun <T : Number> T.dpToPx(): T {
        val value = this.toFloat()
        val result = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value,
            requireContext().resources.displayMetrics
        )

        @Suppress("UNCHECKED_CAST")
        return when (this) {
            is Int -> result.toInt() as T
            is Float -> result as T
            is Double -> result.toDouble() as T
            is Long -> result.toLong() as T
            else -> throw IllegalArgumentException("Unconfirmed Number Type")
        }
    }
}