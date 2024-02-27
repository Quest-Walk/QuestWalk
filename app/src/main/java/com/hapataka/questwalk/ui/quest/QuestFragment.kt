package com.hapataka.questwalk.ui.quest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentQuestBinding
import com.hapataka.questwalk.ui.quest.adapter.QuestAdapter

class QuestFragment : Fragment() {
    private val binding: FragmentQuestBinding by lazy { FragmentQuestBinding.inflate(layoutInflater) }
    private val questAdapter: QuestAdapter by lazy { QuestAdapter() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initQuestRecyclerView()
    }

    private fun initQuestRecyclerView() {
        binding.revQuest.adapter = questAdapter
        questAdapter.submitList(dummySet())
    }

    private fun dummySet(): MutableList<QuestStatsEntity> {
        return mutableListOf(
            QuestStatsEntity(
                "한의원",
                2,
                mapOf("kim1" to R.drawable.ic_dummy1, "kim2" to R.drawable.ic_dummy1, "kim3" to R.drawable.ic_dummy1, "kim4" to R.drawable.ic_dummy1, "kim5" to R.drawable.ic_dummy1)
            ),
            QuestStatsEntity(
                "짜장면",
                3,
                mapOf("kim6" to R.drawable.ic_dummy2, "kim7" to R.drawable.ic_dummy2, "kim8" to R.drawable.ic_dummy2, "kim9" to R.drawable.ic_dummy2, "kim10" to R.drawable.ic_dummy2)
            ),
            QuestStatsEntity(
                "짬뽕",
                1,
                mapOf("kim11" to R.drawable.ic_dummy3, "kim12" to R.drawable.ic_dummy3, "kim13" to R.drawable.ic_dummy3, "kim14" to R.drawable.ic_dummy3, "kim15" to R.drawable.ic_dummy3)
            ),
            QuestStatsEntity(
                "PT",
                4,
                mapOf("kim16" to R.drawable.ic_dummy1, "kim17" to R.drawable.ic_dummy1, "kim18" to R.drawable.ic_dummy1, "kim19" to R.drawable.ic_dummy1, "kim20" to R.drawable.ic_dummy1)
            ),
            QuestStatsEntity(
                "헬스장",
                3,
                mapOf("kim21" to R.drawable.ic_dummy2, "kim22" to R.drawable.ic_dummy2, "kim23" to R.drawable.ic_dummy2, "kim24" to R.drawable.ic_dummy2, "kim25" to R.drawable.ic_dummy2)
            ),
            QuestStatsEntity(
                "병원",
                1,
                mapOf("kim26" to R.drawable.ic_dummy3, "kim27" to R.drawable.ic_dummy3, "kim28" to R.drawable.ic_dummy3, "kim29" to R.drawable.ic_dummy3, "kim30" to R.drawable.ic_dummy3)
            ),
            QuestStatsEntity(
                "핸드폰",
                2,
                mapOf("kim31" to R.drawable.ic_dummy1, "kim32" to R.drawable.ic_dummy1, "kim33" to R.drawable.ic_dummy1, "kim34" to R.drawable.ic_dummy1, "kim35" to R.drawable.ic_dummy1)
            ),
            QuestStatsEntity(
                "치킨",
                1,
                mapOf("kim36" to R.drawable.ic_dummy2, "kim37" to R.drawable.ic_dummy2, "kim38" to R.drawable.ic_dummy2, "kim39" to R.drawable.ic_dummy2, "kim40" to R.drawable.ic_dummy2)
            ),
        )
    }

}