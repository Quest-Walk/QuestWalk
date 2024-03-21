package com.hapataka.questwalk.ui.record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.hapataka.questwalk.databinding.FragmentRecordBinding
import com.hapataka.questwalk.ui.record.adapter.RecordItemAdapter
import com.hapataka.questwalk.ui.record.model.RecordItem
import com.hapataka.questwalk.util.ViewModelFactory

class RecordFragment : Fragment() {
    private val binding by lazy { FragmentRecordBinding.inflate(layoutInflater) }
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private val viewModel by viewModels<RecordViewModel> { ViewModelFactory() }
    private val recordItemAdapter by lazy { RecordItemAdapter(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setObserver()
        getItems()
    }

    private fun initView() {
        initBackButton()
    }

    private fun setObserver() {
        with(viewModel) {
            recordItems.observe(viewLifecycleOwner) {items ->
                initViewPager(items)
            }
            achieveItems.observe(viewLifecycleOwner) {
                recordItemAdapter.achieveItems = it
            }
        }
    }

    private fun getItems() {
        viewModel.getRecordItems()
    }

    private fun initViewPager(recordItems: List<RecordItem>) {
        val tabTitle = listOf("히스토리", "업적")

        recordItemAdapter.items = recordItems
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