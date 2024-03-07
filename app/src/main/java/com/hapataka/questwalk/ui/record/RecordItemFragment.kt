package com.hapataka.questwalk.ui.record

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentRecordItemBinding
import com.hapataka.questwalk.ui.record.adapter.HEADER_TYPE
import com.hapataka.questwalk.ui.record.adapter.RecordDetailAdapter
import com.hapataka.questwalk.ui.record.model.RecordItem
import com.hapataka.questwalk.util.BaseFragment

const val TAG = "item_test"

class RecordItemFragment(val items: List<RecordItem>) : BaseFragment<FragmentRecordItemBinding>(FragmentRecordItemBinding::inflate) {
    private val recordDetailAdapter by lazy { RecordDetailAdapter(requireContext()) }
    private val gridLayoutManager by lazy { GridLayoutManager(requireContext(), 3) }
    private val navController by lazy { (parentFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment).findNavController() }

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
                    Log.i(TAG, "item: ${item}")
                    navController.navigate(R.id.action_frag_record_to_frag_result)
                }
            }
        }.also { recordDetailAdapter.itemClick = it }
    }
}