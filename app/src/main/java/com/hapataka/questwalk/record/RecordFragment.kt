package com.hapataka.questwalk.record

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.hapataka.questwalk.databinding.FragmentRecordBinding
//import com.hapataka.questwalk.record.adapter.RecordItemAdapter

class RecordFragment : Fragment() {
    private val binding by lazy { FragmentRecordBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnNono.setOnClickListener {
            Log.i(TAG,"외안되?")
        }
//        initViewPager()
    }

    private fun initViewPager() {
        val tabTitle = listOf("히스토리", "업적")

//        with(binding.vpRecordContents) {
//            adapter = RecordItemAdapter(requireActivity())
//        }

//        with(binding) {
//            vpRecordContents.adapter = RecordItemAdapter(requireActivity())
//            TabLayoutMediator(tlRecordMenu, vpRecordContents) {tab, position ->
//                tab.text = tabTitle[position]
//            }.attach()
//        }
    }
}