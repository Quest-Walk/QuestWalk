package com.hapataka.questwalk.ui.quest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentQuestDetailBinding
import com.hapataka.questwalk.ui.quest.adapter.QuestDetailRecyclerViewDecoration
import com.hapataka.questwalk.ui.quest.adapter.QuestDetailAdapter
import kotlin.math.round

class QuestDetailFragment : Fragment() {
    private val binding by lazy { FragmentQuestDetailBinding.inflate(layoutInflater) }
    private lateinit var questDetailAdapter: QuestDetailAdapter
    private val navHost by lazy { (parentFragment as NavHostFragment).findNavController() }
    private var completeRate: Double = 0.0
    private var item: QuestData? = null
    private var allUser: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            item = it.getParcelable("item") as? QuestData
            allUser = it.getLong("allUser")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initQuestDetailRecyclerView()
    }

    private fun initViews() {
        completeRate = round((item?.successItems?.size?.toDouble()?.div(allUser))?.times(100) ?: 0.0)

        binding.tvKeyword.text = item?.keyWord
        binding.tvSolve.text = "이 퀘스트는 ${item?.successItems?.size}명이 해결했어요"
        binding.tvSolvePercent.text = "해결 인원${completeRate.toInt()}%"

        binding.ivArrowBack.setOnClickListener {
            navHost.popBackStack()
        }
    }

    private fun initQuestDetailRecyclerView() {
        if (binding.revQuestDetail.itemDecorationCount != 0) {
            binding.revQuestDetail.removeItemDecorationAt(0)
        }

        binding.revQuestDetail.addItemDecoration(QuestDetailRecyclerViewDecoration())

        questDetailAdapter = QuestDetailAdapter {
            val bundle = Bundle().apply {
                putParcelable("SuccessItem",it)
                putDouble("CompleteRate",completeRate)
            }
            navHost.navigate(R.id.action_frag_quest_detail_to_frag_result, bundle)
        }

        binding.revQuestDetail.adapter = questDetailAdapter
        questDetailAdapter.submitList(item?.successItems?.reversed())
    }
}