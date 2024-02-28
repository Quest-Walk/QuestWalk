package com.hapataka.questwalk.ui.quest

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentQuestDetailBinding
import com.hapataka.questwalk.ui.quest.adapter.QuestAdapter
import com.hapataka.questwalk.ui.quest.adapter.QuestAdapterDecoration
import com.hapataka.questwalk.ui.quest.adapter.QuestDetailAdapter

class QuestDetailFragment : Fragment() {
    private val binding: FragmentQuestDetailBinding by lazy { FragmentQuestDetailBinding.inflate(layoutInflater) }
    private lateinit var questDetailAdapter: QuestDetailAdapter
    private var item: QuestStatsEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            item = it.getParcelable("item") as? QuestStatsEntity
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("QuestDetailFragment:","item: $item")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initQuestDetailRecyclerView()
    }

    private fun initViews() {
        binding.tvKeyword.text = item?.keyWord
    }

    private fun initQuestDetailRecyclerView() {
        questDetailAdapter = QuestDetailAdapter {
            // detail page 이동
        }
        binding.revQuestDetail.addItemDecoration(QuestAdapterDecoration())
        binding.revQuestDetail.adapter = questDetailAdapter
        val urlList = item?.successItems?.map { it.value }
        questDetailAdapter.submitList(urlList?.toMutableList())
    }

    companion object {
        @JvmStatic
        fun newInstance(item: QuestStatsEntity) =
            QuestDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("item",item)
                }
            }
    }
}