package com.hapataka.questwalk.ui.fragment.questdetail

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentQuestDetailBinding
import com.hapataka.questwalk.ui.fragment.quest.QuestData
import com.hapataka.questwalk.ui.fragment.questdetail.adapter.QuestDetailAdapter
import com.hapataka.questwalk.ui.fragment.questdetail.adapter.QuestDetailRecyclerViewDecoration
import com.hapataka.questwalk.util.BaseFragment
import kotlin.math.round

class QuestDetailFragment : BaseFragment<FragmentQuestDetailBinding>(FragmentQuestDetailBinding::inflate) {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initQuestDetailRecyclerView()
    }

    private fun initViews() {
        completeRate =
            round((item?.successItems?.size?.toDouble()?.div(allUser))?.times(100) ?: 0.0)

        with(binding) {
            tvKeyword.text = item?.keyWord
            tvSolve.text = "이 퀘스트는 ${item?.successItems?.size}명이 해결했어요"
            tvSolvePercent.text = "해결 인원${completeRate.toInt()}%"
            ivArrowBack.setOnClickListener {
                navHost.popBackStack()
            }
        }
        binding.innerContainer.setPadding()
        requireActivity().setLightBarColor(true)
    }

    private fun initQuestDetailRecyclerView() {
        if (binding.revQuestDetail.itemDecorationCount != 0) {
            binding.revQuestDetail.removeItemDecorationAt(0)
        }
        questDetailAdapter = QuestDetailAdapter { uri, imageView ->
            val bundle = Bundle().apply {
                putString("imageUri", uri)
            }
            navHost.navigate(R.id.action_frag_quest_detail_to_dialog_full_image, bundle)
        }.apply {
            submitList(item?.successItems?.reversed())
        }

        with(binding.revQuestDetail) {
            addItemDecoration(QuestDetailRecyclerViewDecoration(requireContext()))
            adapter = questDetailAdapter
        }
    }
}