package com.hapataka.questwalk.ui.quest

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentQuestBinding
import com.hapataka.questwalk.ui.quest.adapter.QuestListAdapter
import com.hapataka.questwalk.util.BaseFragment

class QuestFragment : BaseFragment<FragmentQuestBinding>(FragmentQuestBinding::inflate) {
    private lateinit var questListAdapter: QuestListAdapter
    private val questViewModel: QuestViewModel by viewModels()
    private val navHost by lazy { (parentFragment as NavHostFragment).findNavController() }
    private var keywords: MutableList<String> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserve()
        initViews()
    }

    private fun setObserve() {
        with(questViewModel) {
            questItems.observe(viewLifecycleOwner) {
                questListAdapter.submitList(it)
            }
//            allUserSize.observe(viewLifecycleOwner) {
//                questListAdapter.setAllUser(it)
//            }
            successKeywords.observe(viewLifecycleOwner) {
                keywords = it
            }
        }
    }

    private fun initViews() {
        initBackButton()
        initSpinner()
        initCompleteButton()
        initQuestRecyclerView()
    }

    private fun initBackButton() {
        binding.ivArrowBack.setOnClickListener {
            navHost.popBackStack()
        }
    }

    private fun initSpinner() {
//        binding.spinnerLevel.selectItemByIndex(0)
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
        questListAdapter = QuestListAdapter(
            requireContext(),
            onClickMoreText = {questData, allUser ->
                val bundle = Bundle().apply {
                    putParcelable("item", questData)
                    putLong("allUser", allUser)
                }
                navHost.navigate(R.id.action_frag_quest_to_frag_quest_detail, bundle)
            },

            onClickView =  {keyWord ->
                val dialog = QuestDialog(keyWord, keywords) {
                    Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
                }

                dialog.show(parentFragmentManager, "QuestDialog")
            }
        )
        binding.revQuest.adapter = questListAdapter
    }
}