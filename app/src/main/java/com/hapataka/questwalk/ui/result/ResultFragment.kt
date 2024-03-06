package com.hapataka.questwalk

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import com.hapataka.questwalk.databinding.FragmentResultBinding
import com.hapataka.questwalk.domain.entity.HistoryEntity
import com.hapataka.questwalk.ui.quest.QuestData
import com.hapataka.questwalk.ui.result.ResultViewModel


class ResultFragment : Fragment() {
    private var _binding : FragmentResultBinding? = null
    private val binding get() = _binding!!
    private val resultViewModel: ResultViewModel by viewModels()
    private var successItem: QuestData.SuccessItem? = null
    private var completeRate: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            successItem = it.getParcelable("SuccessItem") as? QuestData.SuccessItem
            completeRate = it.getDouble("CompleteRate")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataObserve()
        getInfo()
    }

    private fun dataObserve() {
        resultViewModel.resultItem.observe(viewLifecycleOwner) {
            initViews(it)
            resultViewModel.getQuestByKeyword(it.quest)
        }

        resultViewModel.questItem.observe(viewLifecycleOwner) {
            initImageViews(it)
        }
    }

    private fun getInfo() {
        successItem?.let {
            resultViewModel.getResultHistory(it.userId, it.imageUrl)
        }
    }

    private fun initViews(result: HistoryEntity.ResultEntity) {
        with(binding) {
            ivQuestImage.load(result.questImg)
            tvAdvTime.text = result.time
            tvAdvDistance.text = "${result.distance}"
            tvTotalSteps.text = "${result.step}"
            tvCalories.text = "Zero"
            tvQuestKeyword.text = result.quest
            tvCompleteRate.text = "$completeRate"
        }
    }

    private fun initImageViews(questItem: QuestData) {
        val imageList = listOf(
            binding.ivImage1,
            binding.ivImage2,
            binding.ivImage3,
            binding.ivImage4
        )

        for (i in imageList.indices) {
            if (i < questItem.successItems.size) imageList[i].visibility = View.VISIBLE
            else imageList[i].visibility = View.INVISIBLE
        }

        questItem.successItems.reversed().take(4).forEachIndexed { index, successItem ->
            imageList[index].load(successItem.imageUrl)
        }
    }




}