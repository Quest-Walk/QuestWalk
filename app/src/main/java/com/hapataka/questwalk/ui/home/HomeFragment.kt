package com.hapataka.questwalk.ui.home

import android.content.res.ColorStateList
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Chronometer
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.load
import coil.request.ImageRequest
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentHomeBinding
import com.hapataka.questwalk.ui.record.TAG
import kotlinx.coroutines.delay
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
        }
    }

    private fun initView() {

        setQuestState()
        replaceFragmentByImageButton()
        setQuestButtonEvent()
        initBackPressedCallback()
    }

    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private fun replaceFragmentByImageButton() {
        with(binding) {
            btnRecord.setOnClickListener {
                navController.navigate(R.id.action_frag_home_to_frag_record)
            }
            btnMyPage.setOnClickListener {
                navController.navigate(R.id.action_frag_home_to_frag_my_info)
            }
            btnWheather.setOnClickListener {
                navController.navigate(R.id.action_frag_home_to_weatherFragment)
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
                llPlayingContents.visibility = View.VISIBLE
                btnQuestChange.visibility = View.GONE

                //버튼색 이름 및 색깔 변경
                btnQuestStatus.text = "포기하기"
                setBackgroundWidget(btnQuestStatus, R.color.red)
                initQuestStart()

            } else { // 모험이 끝날때!
                clPlayingBottomWidgets.visibility = View.GONE
                llPlayingContents.visibility = View.GONE
                btnQuestChange.visibility = View.VISIBLE


                //버튼색 이름 및 색깔 변경
                btnQuestStatus.text = "모험 시작하기"
                setBackgroundWidget(btnQuestStatus, R.color.green)
                initQuestEnd()
            }
        }
    }

    private fun initQuestStart() {
        val imageLoader = ImageLoader.Builder(requireContext())
            .components {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
        val request = ImageRequest.Builder(requireContext())
            .data(R.drawable.character_move_01)
            .target(binding.ivChrImage)
            .build()

        imageLoader.enqueue(request)

        val animation = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 1f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            )

        animation.duration = 3000
        animation.repeatCount = Animation.INFINITE
        binding.background.startAnimation(animation)
    }

    private fun initQuestEnd() {
        binding.ivChrImage.load(R.drawable.character_01)
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