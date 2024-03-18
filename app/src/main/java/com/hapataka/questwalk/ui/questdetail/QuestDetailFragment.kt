package com.hapataka.questwalk.ui.questdetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentQuestDetailBinding
import com.hapataka.questwalk.ui.quest.QuestData
import com.hapataka.questwalk.ui.questdetail.adapter.QuestDetailAdapter
import com.hapataka.questwalk.ui.questdetail.adapter.QuestDetailRecyclerViewDecoration
import com.hapataka.questwalk.ui.record.TAG
import com.hapataka.questwalk.util.extentions.visible
import kotlin.math.round

class QuestDetailFragment : Fragment() {
    private val binding by lazy { FragmentQuestDetailBinding.inflate(layoutInflater) }
//    private val questDetailAdapter by lazy { QuestDetailAdapter() }
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
        questDetailAdapter = QuestDetailAdapter {uri, imageView ->
//            val extras = FragmentNavigatorExtras(imageView to "pair_image")
//            val bundle = Bundle().apply {
//                putString("imageUri",uri)
//            }
            val fragment = FullImageFragment.newInstance(uri)

            binding.addOnContainer.visible()
            childFragmentManager.beginTransaction()
                .replace(R.id.add_on_container, fragment)
                .addSharedElement(imageView, "pair_image")
                .addToBackStack("dialog")
                .commit()
            Log.d(TAG, "enter: ${imageView.transitionName}")


//            navHost.navigate(R.id.action_frag_quest_detail_to_frag_full_image, bundle, null, extras)
        }.apply {
            submitList(item?.successItems?.reversed())
        }

        with(binding.revQuestDetail) {
            addItemDecoration(QuestDetailRecyclerViewDecoration())
            adapter = questDetailAdapter
        }
    }
}