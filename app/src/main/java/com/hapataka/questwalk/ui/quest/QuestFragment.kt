package com.hapataka.questwalk.ui.quest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentQuestBinding

class QuestFragment : Fragment() {
    private val binding: FragmentQuestBinding by lazy { FragmentQuestBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }
}