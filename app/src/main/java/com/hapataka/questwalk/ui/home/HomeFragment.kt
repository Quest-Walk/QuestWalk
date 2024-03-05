package com.hapataka.questwalk.ui.home

import android.content.res.ColorStateList
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.load
import coil.request.ImageRequest
import com.google.android.material.snackbar.Snackbar
import com.hapataka.questwalk.R
import com.hapataka.questwalk.ui.camera.CameraViewModel
import com.hapataka.questwalk.databinding.FragmentHomeBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val cameraViewModel: CameraViewModel by activityViewModels()
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
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
        setObserver()
    }

    private fun setObserver(){
        cameraViewModel.isSucceed.observe(viewLifecycleOwner) { isSucceed ->
            if (isSucceed == null) return@observe
            if (isSucceed) {
                Snackbar.make(requireView(), "퀘스트 성공!", Snackbar.LENGTH_SHORT).show()
                cameraViewModel.initIsSucceed()
                homeViewModel.isQuestSuccess = true
                setQuestState()
            }
        }
    }

    private fun updateDataView() {
        lifecycleScope.launch {
            if (homeViewModel.getKeyword() == null) homeViewModel.getQuestWithRepository()
            binding.tvQuestTitlePlay.text = homeViewModel.getKeyword()
        }
    }

    private fun initView() {
        initBackground()
        setQuestState()
        replaceFragmentByImageButton()
        setQuestButtonEvent()
        initBackPressedCallback()
    }

    private fun initBackground() {
        with(binding) {
            ivBgLayer1.load(R.drawable.background_day_layer1)
            ivBgLayer2.load(R.drawable.background_night_layer2)
            ivBgLayer3.load(R.drawable.background_night_layer3)
        }
    }

    private fun replaceFragmentByImageButton() {
        with(binding) {
            btnRecord.setOnClickListener {
                navController.navigate(R.id.action_frag_home_to_frag_record)
            }
            btnMyPage.setOnClickListener {
                navController.navigate(R.id.action_frag_home_to_frag_my_info)
            }
            btnWheather.setOnClickListener {
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
            if(homeViewModel.isQuestSuccess){
                //TODO : 퀘스트 성공 후 값 전달 하기
            }
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

                if (homeViewModel.isQuestSuccess) {
                    btnQuestStatus.text = "완료하기"
                    setBackgroundWidget(btnQuestStatus, R.color.green)
                } else {
                    //버튼색 이름 및 색깔 변경
                    btnQuestStatus.text = "포기하기"
                    setBackgroundWidget(btnQuestStatus, R.color.red)
                }
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

        val requestCharacter = ImageRequest.Builder(requireContext())
            .data(R.drawable.character_move_01)
            .target(binding.ivChrImage)
            .build()

        imageLoader.enqueue(requestCharacter)

        binding.ivBgLayer1.startAnimation(setAnimator(0.25f, -0.25f, 10000))
        binding.ivBgLayer2.startAnimation(setAnimator(0.7f, -0.7f, 40000))
        binding.ivBgLayer3.startAnimation(setAnimator(0.25f, -0.25f, 80000))
    }

    private fun setAnimator(fromX: Float, toX: Float, duration: Long): TranslateAnimation {
        val animation = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, fromX,
            Animation.RELATIVE_TO_SELF, toX,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        )

        animation.repeatCount = Animation.INFINITE
        animation.interpolator = LinearInterpolator()
        animation.duration = duration
        return animation
    }

    private fun initQuestEnd() {
        binding.ivChrImage.load(R.drawable.character_01)
        binding.ivBgLayer1.clearAnimation()
        binding.ivBgLayer2.clearAnimation()
        binding.ivBgLayer3.clearAnimation()
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