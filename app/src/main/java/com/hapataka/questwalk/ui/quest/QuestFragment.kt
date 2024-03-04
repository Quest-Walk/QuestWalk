package com.hapataka.questwalk.ui.quest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentQuestBinding
import com.hapataka.questwalk.ui.quest.adapter.QuestAdapter

class QuestFragment : Fragment() {
    private val binding: FragmentQuestBinding by lazy { FragmentQuestBinding.inflate(layoutInflater) }
    private lateinit var questAdapter: QuestAdapter
    private val questViewModel: QuestViewModel by viewModels()
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
        dataObserve()
        initViews()
    }

    private fun dataObserve() {
        questViewModel.questItems.observe(viewLifecycleOwner) {
            questAdapter.submitList(it)
        }
    }

    private fun initViews() {
        initSpinner()
        initQuestRecyclerView()
    }

    private fun initSpinner() {
        binding.spinnerLevel.selectItemByIndex(0)
        binding.spinnerLevel.setOnSpinnerItemSelectedListener<String> { _, _, Index, Level ->
            questViewModel.filterLevel(Index)
        }
    }

    private fun initQuestRecyclerView() {
        val navHost = (parentFragment as NavHostFragment).findNavController()
        val bundle = Bundle()

        binding.ivArrowBack.setOnClickListener {
            navHost.popBackStack()
        }
        questAdapter = QuestAdapter {item ->
            bundle.apply {
                putParcelable("item", item)
            }
            navHost.navigate(R.id.action_frag_quest_to_frag_quest_detail, bundle)
        }
        binding.revQuest.adapter = questAdapter
    }
}