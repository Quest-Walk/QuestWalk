package com.hapataka.questwalk.ui.quest

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentQuestBinding
import com.hapataka.questwalk.ui.quest.adapter.QuestAdapter
import com.hapataka.questwalk.util.BaseFragment

class QuestFragment : BaseFragment<FragmentQuestBinding>(FragmentQuestBinding::inflate) {
    private lateinit var questAdapter: QuestAdapter
    private val questViewModel: QuestViewModel by viewModels()
    private val navHost by lazy { (parentFragment as NavHostFragment).findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataObserve()
        initViews()
    }

    private fun dataObserve() {
        questViewModel.questItems.observe(viewLifecycleOwner) {
            questAdapter.submitList(it)
        }
        questViewModel.allUserSize.observe(viewLifecycleOwner) {
            questAdapter.setAllUser(it)
        }
    }

    private fun initViews() {
        initArrowBack()
        initSpinner()
        initCompleteButton()
        initQuestRecyclerView()
    }

    private fun initArrowBack() {
        binding.ivArrowBack.setOnClickListener {
            navHost.popBackStack()
        }
    }

    private fun initSpinner() {
        binding.spinnerLevel.selectItemByIndex(0)
        binding.spinnerLevel.setOnSpinnerItemSelectedListener<String> { _, _, Index, Level ->
            questViewModel.filterLevel(Index)
        }
    }

    private fun initCompleteButton() {
        binding.ivComplete.setOnClickListener {
            if (binding.ivCheck.isVisible) {
                binding.ivCheck.visibility = View.INVISIBLE
                questViewModel.filterComplete(false)
            } else {
                binding.ivCheck.visibility = View.VISIBLE
                questViewModel.filterComplete(true)
            }
        }
    }

    private fun initQuestRecyclerView() {
        questAdapter = QuestAdapter(
            onClickMoreText = {questData, allUser ->
                val bundle = Bundle().apply {
                    putParcelable("item", questData)
                    putLong("allUser", allUser)
                }
                navHost.navigate(R.id.action_frag_quest_to_frag_quest_detail, bundle)
            },

            onClickView =  {keyWord ->
                val dialog = QuestDialog(keyWord)
                dialog.show(parentFragmentManager, "QuestDialog")
            }
        )
        binding.revQuest.adapter = questAdapter
    }
}