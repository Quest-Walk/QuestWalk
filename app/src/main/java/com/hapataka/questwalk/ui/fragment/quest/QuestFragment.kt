package com.hapataka.questwalk.ui.fragment.quest

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentQuestBinding
import com.hapataka.questwalk.ui.fragment.quest.adapter.QuestListAdapter
import com.hapataka.questwalk.util.BaseFragment
import com.hapataka.questwalk.util.ViewModelFactory

class QuestFragment : BaseFragment<FragmentQuestBinding>(FragmentQuestBinding::inflate) {
    private lateinit var questListAdapter: QuestListAdapter
    private val questViewModel: QuestViewModel by viewModels { ViewModelFactory() }
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
                questListAdapter.submitList(list) {
                    binding.revQuest.scrollToPosition(0)
//                    binding.loading.visibility = View.INVISIBLE
                }
            }
            successKeywords.observe(viewLifecycleOwner) {
                keywords = it
            }
        }
    }

    private fun initViews() {
        initBackButton()
        initTabButton()
        initCompleteButton()
        initQuestRecyclerView()
        binding.innerContainer.setPadding()
        requireActivity().setLightBarColor(false)
    }

    private fun initBackButton() {
        binding.ivArrowBack.setOnClickListener {
            navHost.popBackStack()
        }
    }

    private fun initTabButton() {
        with(binding) {
            val tabList = mutableListOf(tvAll, tvLv1, tvLv2, tvLv3)

            tvAll.isSelected = true
            tabList.forEachIndexed { index, tab ->
                tab.setOnClickListener {
                    questViewModel.filterLevel(index)
                    tabList.map { it.isSelected = false }
                    tab.isSelected = !tab.isSelected
                }
            }
        }
    }

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
            onClickMoreText = {questData ->
                val bundle = Bundle().apply {
                    putParcelable("item", questData)
                    putLong("allUser", questData.allUser)
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