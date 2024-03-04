package com.hapataka.questwalk.ui.quest

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentQuestDetailBinding
import com.hapataka.questwalk.ui.quest.adapter.QuestAdapter
import com.hapataka.questwalk.ui.quest.adapter.QuestAdapterDecoration
import com.hapataka.questwalk.ui.quest.adapter.QuestDetailAdapter

class QuestDetailFragment : Fragment() {
    private val binding by lazy { FragmentQuestDetailBinding.inflate(layoutInflater) }
    private lateinit var questDetailAdapter: QuestDetailAdapter
    private val item by lazy { arguments?.getParcelable("item") as? QuestData}
    private val navHost by lazy { (parentFragment as NavHostFragment).findNavController() }

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
        binding.tvKeyword.text = item?.keyWord
        binding.ivArrowBack.setOnClickListener {
            navHost.popBackStack()
        }
    }

    private fun initQuestDetailRecyclerView() {

        questDetailAdapter = QuestDetailAdapter {
            // detail page 이동
        }
        binding.revQuestDetail.addItemDecoration(QuestAdapterDecoration())
        binding.revQuestDetail.adapter = questDetailAdapter
        val urlList = item?.successItems?.map { it.imageUrl }
        questDetailAdapter.submitList(urlList?.toMutableList())
    }
}