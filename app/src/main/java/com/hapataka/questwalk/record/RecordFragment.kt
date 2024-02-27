package com.hapataka.questwalk.record

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hapataka.questwalk.databinding.FragmentRecordBinding
import com.hapataka.questwalk.record.adapter.RecordItemAdapter

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
        with(binding.vpRecordContents) {
            adapter = RecordItemAdapter(requireActivity())
        }
    }
}