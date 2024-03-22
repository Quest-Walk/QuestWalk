package com.hapataka.questwalk.ui.home

import android.content.Context
import android.content.pm.PackageManager
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
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
import com.hapataka.questwalk.ui.home.dialog.StopPlayDialog
import com.hapataka.questwalk.ui.mainactivity.MainViewModel
import com.hapataka.questwalk.ui.mainactivity.QUEST_START
import com.hapataka.questwalk.ui.mainactivity.QUEST_STOP
import com.hapataka.questwalk.ui.mainactivity.QUEST_SUCCESS
import com.hapataka.questwalk.ui.result.QUEST_KEYWORD
import com.hapataka.questwalk.ui.result.REGISTER_TIME
import com.hapataka.questwalk.ui.result.USER_ID
import com.hapataka.questwalk.util.BaseFragment
import com.hapataka.questwalk.util.ViewModelFactory
import com.hapataka.questwalk.util.extentions.SIMPLE_TIME
import com.hapataka.questwalk.util.extentions.convertKm
import com.hapataka.questwalk.util.extentions.convertTime
import com.hapataka.questwalk.util.extentions.gone
import com.hapataka.questwalk.util.extentions.invisible
import com.hapataka.questwalk.util.extentions.visible
import kotlinx.coroutines.Job
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

    private var currentDistance = -1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.setRandomKeyword()
    }

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
    }

    private fun setup() {
        checkPermissions()
        setObserver()
        initBackPressedCallback()
        setUid()
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

    private fun initQuestButton() {
        binding.btnToggleQuestState.setOnClickListener {
            mainViewModel.togglePlay()
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
        }
        with(mainViewModel) {
            currentKeyword.observe(viewLifecycleOwner) {
                binding.tvQuestKeyword.text = it
            }
            playState.observe(viewLifecycleOwner) { state ->
                updateWithState(state)
            }
            durationTime.observe(viewLifecycleOwner) {
                binding.tvQuestTime.text = it.convertTime(SIMPLE_TIME)
            }
            totalDistance.observe(viewLifecycleOwner) {
                binding.tvQuestDistance.text = it.convertKm()
                currentDistance = it
            }
            totalStep.observe(viewLifecycleOwner) { step ->
                binding.tvQuestPlaying.text = "${step}걸음"
            }
            isStop.observe(viewLifecycleOwner) { isStop ->
                if (isStop) {
                    showStopDialog()
                }
            }
        }
    }

    private fun showStopDialog() {
        val dialog = StopPlayDialog(currentDistance,
            { activePositive() },
            { activeNegative() }
        )

        dialog.show(parentFragmentManager, "HomeDialog")
    }

    private fun activePositive() {
        lifecycleScope.launch {
            mainViewModel.stopPlay { uid, registerAt ->
                val bundle = Bundle().apply {
                    putString(USER_ID, uid)
                    putString(REGISTER_TIME, registerAt)
                    putString(QUEST_KEYWORD, binding.tvQuestKeyword.text.toString())
                }
                navController.navigate(R.id.action_frag_home_to_frag_result, bundle)
            }
        }
    }

    private fun activeNegative() {
        mainViewModel.resumePlay()
    }

    private fun updateWithState(playState: Int) {
        updateViews(playState)
        toggleStepSensor(playState)
    }

    private fun updateViews(playState: Int) {
        with(binding) {
            if (playState == QUEST_STOP) {
                ibCamera.gone()
                llPlayingContents.gone()
                tvQuestChange.visible()
                btnToggleQuestState.text = "모험 시작하기"
                setBackgroundWidget(btnToggleQuestState, R.color.button)
                endBackgroundAnim()
                btnQuestChange.isEnabled = true
                return
            }

            if (playState == QUEST_START) {
                ibCamera.visible()
                llPlayingContents.visible()
                tvQuestChange.invisible()
                btnToggleQuestState.text = "포기하기"
                setBackgroundWidget(btnToggleQuestState, R.color.red)
                startBackgroundAnim()
                btnQuestChange.isEnabled = false
                return
            }

            if (playState == QUEST_SUCCESS) {
                ibCamera.gone()
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

    private fun toggleStepSensor(playState: Int) {
        if (playState == QUEST_START) {
            initStepSensor()
            return
        }

        if (playState == QUEST_STOP) {
            sensorManager.unregisterListener(sensorListener, stepSensor)
            return
        }
    }

    private var characterMove: Job? = null
    private fun startBackgroundAnim() {
        characterMove = lifecycleScope.launch {
            while (true) {
                val imageLoader = ImageLoader.Builder(requireContext())
                    .components {
                        if (SDK_INT >= 28) {
                            add(ImageDecoderDecoder.Factory())
                        } else {
                            add(GifDecoder.Factory())
                        }
                    }.build()
                val requestCharacter = ImageRequest.Builder(requireContext())
                    .data(R.drawable.character_move_01)
                    .target(binding.ivChrImage)
                    .placeholder(R.drawable.character_01)
                    .build()

                imageLoader.enqueue(requestCharacter)
                binding.motionLayout.scene.duration = 2000
                binding.motionLayout.transitionToEnd()
                delay(7000L)
                binding.motionLayout.scene.duration = 3000
                binding.motionLayout.transitionToStart()
                delay(3000L)
            }
        }
        setBackgroundPosition(ANIM_POSITION)
        with(binding) {
            ivBgLayer1.startAnimation(setAnimator(0.25f, -0.25f, 10000))
            ivBgLayer2.startAnimation(setAnimator(0.7f, -0.7f, 40000))
            ivBgLayer3.startAnimation(setAnimator(0.25f, -0.25f, 80000))
        }
    }

    private fun endBackgroundAnim() {
        with(binding) {
            ivBgLayer1.clearAnimation()
            ivBgLayer2.clearAnimation()
            ivBgLayer3.clearAnimation()
            setBackgroundPosition(STOP_POSITION)
            ivBgLayer1.translationX = 2115f
            ivBgLayer2.translationX = -2800f
            ivBgLayer3.translationX = 2115f
        }
        characterMove?.cancel()
        characterMove = lifecycleScope.launch {
            binding.motionLayout.scene.duration = 0
            binding.motionLayout.transitionToStart()
            binding.ivChrImage.load(R.drawable.character_01)
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
    }

    private val sensorListener by lazy {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                val sensor = event!!.sensor

                if (sensor.type == Sensor.TYPE_STEP_DETECTOR) {
                    mainViewModel.countUpStep()
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    private val stepSensor by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) }

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

    val TAG = "permission_test"
    private fun checkPermissions() {
        val camera = android.Manifest.permission.CAMERA
        val activity = android.Manifest.permission.ACTIVITY_RECOGNITION
        val location = android.Manifest.permission.ACCESS_FINE_LOCATION

        if (checkPermission(activity)) {
            Log.i(TAG, "신체정보 퍼미션 없음")
        }

        if (checkPermission(camera)) {
            Log.i(TAG, "카메라 퍼미션 없음")
        }

        if (checkPermission(location)) {
            Log.i(TAG, "위치 퍼미션 없음")
        }

//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                android.Manifest.permission.CAMERA
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            Log.i(TAG, "퍼미션 없음")
//            checkCamera()
//            checkLocation()
//        }
    }

    private fun checkPermission(name: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            name
        ) != PackageManager.PERMISSION_GRANTED
    }


    private fun checkCamera() {
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted.not()) {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                    return@registerForActivityResult
                }
                showDialog(
                    "Quest Keyword를 사용하려면\n카메라 권한이 필요합니다.",
                ) {
                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                }
            }
        }

        permissionLauncher.launch(android.Manifest.permission.CAMERA)
    }

    private fun checkLocation() {
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted.not()) {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    return@registerForActivityResult
                }

                if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    permissionLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    return@registerForActivityResult
                }
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    permissionLauncher.launch(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    return@registerForActivityResult
                }

                showDialog(
                    "Quest Keyword를 사용하려면\n위치 권한이 필요합니다.",
                ) {
                    permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    permissionLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    permissionLauncher.launch(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                }
            }
        }

        permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    private fun showDialog(msg: String, callback: () -> Unit) {
        val dialog = PermissionDialog(msg, callback)

        dialog.show(parentFragmentManager, "permissionDialog")
    }

    private fun setUid() {
        lifecycleScope.launch {
            viewModel.setUserInfo()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        characterMove?.cancel()
    }
}