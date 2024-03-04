package com.hapataka.questwalk.ui.home

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class HomeFragment : Fragment() {


    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var binding: FragmentHomeBinding
    private var backPressedOnce = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        updateDataView()

    }

    private fun updateDataView() {
        lifecycleScope.launch {
            if (homeViewModel.getKeyword() == null) homeViewModel.getQuestWithRepository()
            binding.tvQuestTitlePlay.text = homeViewModel.getKeyword()
            binding.tvQuestTitleWait.text = homeViewModel.getKeyword()
        }
    }

    private fun initView() {

        setQuestState()
        replaceFragmentByImageButton()
        setQuestButtonEvent()
        initBackPressedCallback()
    }

    /**
     *  프래그먼트 간의 이동
     */

    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private fun replaceFragmentByImageButton() {
        with(binding) {
            ibHistory.setOnClickListener {
                navController.navigate(R.id.action_frag_home_to_frag_record)
            }
            ibMyPage.setOnClickListener {
                navController.navigate(R.id.action_frag_home_to_frag_my_info)
            }
            ibWheather.setOnClickListener {
                // TODO : wheatherFragment 이동
            }
            ibCamera.setOnClickListener {
                navController.navigate(R.id.action_frag_home_to_frag_camera)
            }
            btnQuestChange.setOnClickListener {
                navController.navigate(R.id.action_frag_home_to_frag_quest)
            }
        }
    }


    private fun setQuestButtonEvent() {
        binding.btnQuestStatus.setOnClickListener {
            homeViewModel.isPlay = !homeViewModel.isPlay
            setQuestState()
        }
    }

    private fun setQuestState() {
        with(binding) {
            if (homeViewModel.isPlay) { // 모험 시작!
                clPlayingBottomWidgets.visibility = View.VISIBLE
                clPlayingUpperWidgets.visibility = View.VISIBLE
                clWaitingBottomWidgets.visibility = View.GONE

                //버튼색 이름 및 색깔 변경
                btnQuestStatus.text = "포기하기"
                setBackgroundWidget(btnQuestStatus, R.color.red)

            } else { // 모험이 끝날때!
                clPlayingBottomWidgets.visibility = View.GONE
                clPlayingUpperWidgets.visibility = View.GONE
                clWaitingBottomWidgets.visibility = View.VISIBLE


                //버튼색 이름 및 색깔 변경
                btnQuestStatus.text = "모험 시작하기"
                setBackgroundWidget(btnQuestStatus, R.color.green)
            }
        }
    }

    // backgroundTint 값 변경
    private fun setBackgroundWidget(view: View, colorResource: Int) {
        val colorStateList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                colorResource
            )
        )
        ViewCompat.setBackgroundTintList(view, colorStateList)
    }

    private fun initBackPressedCallback() {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedOnce) {
                    requireActivity().finish()
                    return
                }
                backPressedOnce = true
                Toast.makeText(requireContext(), "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                lifecycleScope.launch {
                    delay(2000)
                    backPressedOnce = false
                }

            }
        }.also {
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, it)
        }
    }
}