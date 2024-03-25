package com.hapataka.questwalk.ui.fragment.quest

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentQuestBinding
import com.hapataka.questwalk.ui.fragment.quest.adapter.QuestListAdapter
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
                val list = it + mutableListOf(QuestData())
                questListAdapter.submitList(list)
            }
            successKeywords.observe(viewLifecycleOwner) {
                keywords = it
            }
        }
    }

    private fun initViews() {
        initBackButton()
//        initSpinner()
        initTabButton()
        initCompleteButton()
        initQuestRecyclerView()
        binding.innerContainer.setPadding()
        requireActivity().setLightBarColor(true)
    }

    private fun initBackButton() {
        binding.ivArrowBack.setOnClickListener {
            navHost.popBackStack()
        }
    }

    private fun initTabButton() {
        with(binding) {
            val tabList = mutableListOf(tvAll, tvLv1, tvLv2, tvLv3)

            binding.tvAll.isSelected = true
            tabList.forEachIndexed { index, tab ->
                tab.setOnClickListener {
                    questViewModel.filterLevel(index)
                    tabList.map { it.isSelected = false }
                    tab.isSelected = !tab.isSelected
                }
            }
        }
    }

//    private fun initSpinner() {
//        binding.spinnerLevel.selectItemByIndex(0)
//        binding.spinnerLevel.setOnSpinnerItemSelectedListener<String> { _, _, Index, Level ->
//            questViewModel.filterLevel(Index)
//        }
//    }

    private fun initCompleteButton() {
//        binding.constrainComplete.setOnClickListener {
//            if (binding.ivCheck.isVisible) {
//                binding.ivCheck.visibility = View.INVISIBLE
//                questViewModel.filterComplete(false)
//            } else {
//                binding.ivCheck.visibility = View.VISIBLE
//                questViewModel.filterComplete(true)
//            }
//        }
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
                val dialog = QuestDialog(keyWord, keywords)

                dialog.show(parentFragmentManager, "QuestDialog")
            }
        )
        binding.revQuest.adapter = questListAdapter
        binding.revQuest.itemAnimator = null
    }
}