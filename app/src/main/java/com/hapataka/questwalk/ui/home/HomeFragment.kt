package com.hapataka.questwalk.ui.home

import android.content.Context
import android.content.res.ColorStateList
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
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
import com.hapataka.questwalk.ui.mainactivity.MainViewModel
import com.hapataka.questwalk.ui.mainactivity.QUEST_START
import com.hapataka.questwalk.ui.mainactivity.QUEST_STOP
import com.hapataka.questwalk.ui.mainactivity.QUEST_SUCCESS
import com.hapataka.questwalk.ui.record.TAG
import com.hapataka.questwalk.ui.result.QUEST_KEYWORD
import com.hapataka.questwalk.ui.result.REGISTER_TIME
import com.hapataka.questwalk.ui.result.USER_ID
import com.hapataka.questwalk.util.BaseFragment
import com.hapataka.questwalk.util.LoadingDialogFragment
import com.hapataka.questwalk.util.ViewModelFactory
import com.hapataka.questwalk.util.extentions.SIMPLE_TIME
import com.hapataka.questwalk.util.extentions.convertKm
import com.hapataka.questwalk.util.extentions.convertTime
import com.hapataka.questwalk.util.extentions.gone
import com.hapataka.questwalk.util.extentions.invisible
import com.hapataka.questwalk.util.extentions.visible
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val STOP_POSITION = 0
const val ANIM_POSITION = 1

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private val mainViewModel: MainViewModel by activityViewModels { ViewModelFactory(requireContext()) }
    private val viewModel: HomeViewModel by activityViewModels { ViewModelFactory() }
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private var backPressedOnce = false

    private val sensorManager by lazy {
        requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

//    private lateinit var locationPermission: ActivityResultLauncher<Array<String>>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setup()
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkCurrentTime()
    }

    private fun initViews() {
        initBackground()
        initNaviButtons()
        initQuestButton()
//        checkPermission()
    }

    private fun setup() {
        setObserver()
        initBackPressedCallback()
    }

    private fun initBackground() {
        with(binding) {
            ivBgLayer1.load(R.drawable.background_day_layer1)
            setBackgroundPosition(STOP_POSITION)
        }
    }

    private fun initNaviButtons() {
        with(binding) {
            btnRecord.setOnClickListener {
                navController.navigate(R.id.action_frag_home_to_frag_record)
            }
            btnMyPage.setOnClickListener {
                navController.navigate(R.id.action_frag_home_to_frag_my_info)
            }
            btnWheather.setOnClickListener {
                "준비중이에요".showToast()
            }
            ibCamera.setOnClickListener {
                navController.navigate(R.id.action_frag_home_to_frag_camera)
            }
            btnQuestChange.setOnClickListener {
                navController.navigate(R.id.action_frag_home_to_frag_quest)
            }
        }
    }

    private fun initQuestButton() {
        binding.btnToggleQuestState.setOnClickListener {
            mainViewModel.togglePlay {
                val bundle = Bundle()
                mainViewModel.moveToResult { uid, currentTime ->
                    bundle.apply {
                        putString(USER_ID, uid)
                        putString(QUEST_KEYWORD, binding.tvQuestKeyword.text.toString())
                        putString(REGISTER_TIME, currentTime)
                    }
                }
                navController.navigate(R.id.action_frag_home_to_frag_result)
            }
        }
    }

    private fun setObserver() {
        with(viewModel) {
            isNight.observe(viewLifecycleOwner) { night ->
                if (night) {
                    binding.ivBgLayer2.load(R.drawable.background_night_layer2)
                    binding.ivBgLayer3.load(R.drawable.background_night_layer3)
                } else {
                    binding.ivBgLayer2.load(R.drawable.background_day_layer2)
                    binding.ivBgLayer3.load(R.drawable.background_day_layer3)
                }
            }
            totalStep.observe(viewLifecycleOwner) { step ->
                binding.tvQuestPlaying.text = "${step}걸음"
            }
        }
        with(mainViewModel) {
            currentKeyword.observe(viewLifecycleOwner) {
                binding.tvQuestKeyword.text = it
            }
            playState.observe(viewLifecycleOwner) { state ->
                toggleViews(state)
                Log.i(TAG, "playstate: $state")
//                toggleLocation(it)
            }
            durationTime.observe(viewLifecycleOwner) {
                binding.tvQuestTime.text = it.convertTime(SIMPLE_TIME)
            }
            totalDistance.observe(viewLifecycleOwner) {
                binding.tvQuestDistance.text = it.convertKm()
            }
            isLoading.observe(viewLifecycleOwner) { isLoading ->
                if (isLoading) {
                    LoadingDialogFragment().show(parentFragmentManager, "loadingDialog")
                } else {
                    val loadingFragment =
                        parentFragmentManager.findFragmentByTag("loadingDialog") as? LoadingDialogFragment
                    loadingFragment?.dismiss()
                }
            }
        }
    }

    private fun showHomeDialog() {
        val dialog = HomeDialog {
            viewModel.toggleIsPlay()
        }
        dialog.show(parentFragmentManager, "HomeDialog")
    }

    private fun toggleViews(isPlay: Int) {
        with(binding) {
            if (isPlay == QUEST_STOP) {
                ibCamera.gone()
                llPlayingContents.gone()
                tvQuestChange.visible()
                btnToggleQuestState.text = "모험 시작하기"
                setBackgroundWidget(btnToggleQuestState, R.color.button)
                endBackgroundAnim()
                btnQuestChange.isEnabled = true
                return
            }

            if (isPlay == QUEST_START) {
                ibCamera.visible()
                llPlayingContents.visible()
                tvQuestChange.invisible()
                btnToggleQuestState.text = "포기하기"
                setBackgroundWidget(btnToggleQuestState, R.color.red)
                startBackgroundAnim()
                btnQuestChange.isEnabled = false
                return
            }

            if (isPlay == QUEST_SUCCESS) {
                ibCamera.visible()
                llPlayingContents.visible()
                tvQuestChange.invisible()
                btnToggleQuestState.text = "완료하기"
                setBackgroundWidget(btnToggleQuestState, R.color.green)
                startBackgroundAnim()
                btnQuestChange.isEnabled = false
                mainViewModel.setSnackBarMsg("퀘스트 성공!")
                return
            }
        }
    }

    private fun toggleLocation(isPlay: Boolean) {
        if (isPlay) {
//            updateLocation()
            initStepSensor()
        } else {
            sensorManager.unregisterListener(sensorListener, stepSensor)
        }
    }

    private fun startBackgroundAnim() {
//        val charId = viewModel.charNum.value ?: 1
//        val movingCharacter = when(charId) {
//            1->R.drawable.character_move_01
//            else -> R.drawable.character_move_01
//        }
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
            //.data(movingCharacter)
            .data(R.drawable.character_move_01)
            .target(binding.ivChrImage)
            .build()

        imageLoader.enqueue(requestCharacter)
        setBackgroundPosition(ANIM_POSITION)
        with(binding) {
            ivBgLayer1.startAnimation(setAnimator(0.25f, -0.25f, 10000))
            ivBgLayer2.startAnimation(setAnimator(0.7f, -0.7f, 40000))
            ivBgLayer3.startAnimation(setAnimator(0.25f, -0.25f, 80000))
        }
    }

    private fun endBackgroundAnim() {
//        val charId = viewModel.charNum.value ?: 1
//        val character = when(charId) {
//            1 -> R.drawable.character_01
//            else -> R.drawable.character_01
//        }

        with(binding) {
//            ivChrImage.load(character)
            ivChrImage.load(R.drawable.character_01)
            ivBgLayer1.clearAnimation()
            ivBgLayer2.clearAnimation()
            ivBgLayer3.clearAnimation()
            setBackgroundPosition(STOP_POSITION)
            ivBgLayer1.translationX = 2115f
            ivBgLayer2.translationX = -2800f
            ivBgLayer3.translationX = 2115f
        }
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

    private fun initStepSensor() {
        if (stepSensor == null) {
            "No sensor detected on this device".showToast()
            return
        }
        sensorManager.registerListener(sensorListener, stepSensor, SensorManager.SENSOR_DELAY_UI)

        Log.d(TAG, "setpSensor: ${sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)}")
    }

    private val sensorListener by lazy {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                val sensor = event!!.sensor

                if (sensor.type == Sensor.TYPE_STEP_DETECTOR) {
                    viewModel.updateStep()
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    private val stepSensor by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) }

    private fun makeSensorListener(): SensorEventListener {
        return object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                Log.i(TAG, "event: $event")
                val sensor = event!!.sensor

                if (sensor.type == Sensor.TYPE_STEP_DETECTOR) {
                    viewModel.updateStep()
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    private fun String.showToast() {
        Toast.makeText(requireContext(), this, Toast.LENGTH_SHORT).show()
    }

    private fun setBackgroundPosition(positionState: Int) {
        with(binding) {
            when (positionState) {
                STOP_POSITION -> {
                    ivBgLayer1.translationX = 2115f
                    ivBgLayer2.translationX = -2800f
                    ivBgLayer3.translationX = 2115f
                }

                ANIM_POSITION -> {
                    ivBgLayer1.translationX = 0f
                    ivBgLayer2.translationX = 0f
                    ivBgLayer3.translationX = 0f
                }
            }
        }
    }


    //    private fun checkPermission() {
//        locationPermission = registerForActivityResult(
//            ActivityResultContracts.RequestMultiplePermissions()
//        ) { permissions ->
//            when {
//                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
//                    // Precise location access granted.
//                }
//
//                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
//                    // Only approximate location access granted.
//                }
//
//                else -> {
//                    // No location access granted.
//                }
//            }
//        }
//
//        //권한 요청
//        locationPermission.launch(
//            arrayOf(
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            )
//        )
//    }
}