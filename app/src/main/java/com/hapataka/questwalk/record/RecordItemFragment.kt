package com.hapataka.questwalk.record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentRecordItemBinding

class RecordItemFragment : Fragment() {
    private val binding by lazy { FragmentRecordItemBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}