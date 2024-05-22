package com.hapataka.questwalk.ui.record

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.hapataka.questwalk.databinding.FragmentRecordBinding
import com.hapataka.questwalk.ui.fragment.record.adapter.RecordItemAdapter
import com.hapataka.questwalk.ui.fragment.record.model.RecordItem
import com.hapataka.questwalk.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecordFragment : BaseFragment<FragmentRecordBinding>(FragmentRecordBinding::inflate) {
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private val viewModel by viewModels<RecordViewModel> ()
    private lateinit var recordItemAdapter: RecordItemAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setObserver()
        getItems()
    }

    private fun initView() {
        initBackButton()
        recordItemAdapter = RecordItemAdapter(requireActivity())
        binding.innerContainer.setPadding()
        requireActivity().setLightBarColor(true)
    }

    private fun setObserver() {
        with(viewModel) {
            recordItems.observe(viewLifecycleOwner) { items ->
                if (items != recordItemAdapter.items) {
                    initViewPager(items)
                }
            }
            achieveItems.observe(viewLifecycleOwner) {
                recordItemAdapter.achieveItems = it
            }
        }
    }

    private fun getItems() {
        viewModel.getRecordItems()
    }

    private fun initViewPager(itemList: List<RecordItem>) {
        recordItemAdapter.items = itemList

        val tabTitle = listOf("히스토리", "업적")

        with(binding) {
            vpRecordContents.apply {
                adapter = recordItemAdapter
                isSaveEnabled = false
            }
            TabLayoutMediator(tlRecordMenu, vpRecordContents) { tab, position ->
                tab.text = tabTitle[position]
            }.attach()
        }
    }

    private fun initBackButton() {
        binding.btnBack.setOnClickListener {
            navController.popBackStack()
        }
    }
}