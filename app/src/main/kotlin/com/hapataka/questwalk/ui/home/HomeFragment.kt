package com.hapataka.questwalk.ui.home

import android.Manifest
import android.Manifest.permission
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.hapataka.questwalk.R
import com.hapataka.questwalk.core.ui.R.drawable
import com.hapataka.questwalk.core.ui.component.Character
import com.hapataka.questwalk.core.ui.component.HorizontalScrollingBackground
import com.hapataka.questwalk.databinding.FragmentHomeBinding
import com.hapataka.questwalk.ui.common.BaseFragment
import com.hapataka.questwalk.ui.home.dialog.PermissionDialog
import com.hapataka.questwalk.ui.home.dialog.StopPlayDialog
import com.hapataka.questwalk.ui.main.MainViewModel
import com.hapataka.questwalk.ui.main.QUEST_START
import com.hapataka.questwalk.ui.main.QUEST_STOP
import com.hapataka.questwalk.ui.main.QUEST_SUCCESS
import com.hapataka.questwalk.ui.result.QUEST_KEYWORD
import com.hapataka.questwalk.ui.result.REGISTER_TIME
import com.hapataka.questwalk.ui.result.USER_ID
import com.hapataka.questwalk.util.OnSingleClickListener
import com.hapataka.questwalk.util.extentions.SIMPLE_TIME
import com.hapataka.questwalk.util.extentions.convertKm
import com.hapataka.questwalk.util.extentions.convertTime
import com.hapataka.questwalk.util.extentions.gone
import com.hapataka.questwalk.util.extentions.invisible
import com.hapataka.questwalk.util.extentions.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val STOP_POSITION = 0
const val ANIM_POSITION = 1

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: HomeViewModel by viewModels()
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
        loadInitialSetting()
        initViews()
        setup()
    }

    private fun loadInitialSetting() {
        setObserver()
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkCurrentTime()
    }

    private fun initViews() {
        initAnimedImage()
        initNaviButtons()
        initQuestButton()
        checkPermissions()
        binding.innerContainer.setPadding()
        requireActivity().setLightBarColor(false)
    }

    private fun setup() {
        initBackPressedCallback()
        setUid()
    }

    private fun initAnimedImage() {
        binding.cvBg.setContent {
            val playState by mainViewModel.playState.observeAsState()
            val isNight by viewModel.isNight.observeAsState(false)
            var animState by remember { mutableStateOf(false) }

            LaunchedEffect(playState) {
                animState = playState != QUEST_STOP
            }

            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                HorizontalScrollingBackground(
                    isAnimate = animState,
                    modifier = Modifier
                        .fillMaxSize(),
                    imgId = if (isNight) drawable.bg_landscape_night else drawable.bg_landscape_day,
                    duration = 160000
                )
                HorizontalScrollingBackground(
                    isAnimate = animState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f)
                        .align(Alignment.TopCenter)
                        .padding(top = 80.dp),
                    imgId = if (isNight) drawable.bg_starts else R.drawable.background_day_layer2,
                    duration = 40000
                )
                HorizontalScrollingBackground(
                    isAnimate = animState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.4f)
                        .align(Alignment.BottomCenter),
                    imgId = drawable.bg_ground,
                    duration = 15000
                )

                Character(
                    character = Character.BEAR,
                    isAnimate = animState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxHeight(0.35f)
                        .padding(bottom = 100.dp)
                )
            }
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
        binding.btnToggleQuestState.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                mainViewModel.togglePlay()
            }
        })
    }

    private fun setObserver() {
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
                tvToggleQuestState.text = "모험 시작하기"
                btnToggleQuestState.load(R.drawable.btn_quest_default)
                btnQuestChange.isEnabled = true
                return
            }

            if (playState == QUEST_START) {
                ibCamera.visible()
                llPlayingContents.visible()
                tvQuestChange.invisible()
                tvToggleQuestState.text = "포기하기"
                btnToggleQuestState.load(R.drawable.btn_quest_give_up)
                btnQuestChange.isEnabled = false
                return
            }

            if (playState == QUEST_SUCCESS) {
                ibCamera.gone()
                llPlayingContents.visible()
                tvQuestChange.invisible()
                tvToggleQuestState.text = "완료하기"
                btnToggleQuestState.load(R.drawable.btn_quest_success)
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

    private fun makeResultLauncher() {
        activityResultLauncher = registerForActivityResult(contract) { permissions ->
            if (permissions.values.all { it }) {
                return@registerForActivityResult
            }

            val deniedArray = permissions.keys.toTypedArray()
            val needPermission = makePermissionToText(deniedArray)
            val isShowRationales = mutableListOf<Boolean>()


            deniedArray.forEach {
                isShowRationales += shouldShowRequestPermissionRationale(it)
            }

            if (isShowRationales.any { it }) {
                val dialog = PermissionDialog(
                    "퀘스트 워크를 사용하기 위해서는\n${needPermission}이 필요합니다",
                    negativeCallback = { requireActivity().finish() },
                    positiveCallback = { activityResultLauncher.launch(deniedArray) }
                )

                dialog.show(parentFragmentManager, "permission")
                return@registerForActivityResult
            }

            val dialog = PermissionDialog(
                "퀘스트 워크를 사용하기 위해서는\n${needPermission}이 필요합니다",
                negativeCallback = { requireActivity().finish() },
                positiveCallback = { activityResultLauncher.launch(deniedArray) }
            )

            dialog.show(parentFragmentManager, "eiri")
            return@registerForActivityResult
        }
    }

    private fun checkPermissions() {
        makeResultLauncher()

        var requestList = arrayOf<String>()

        if (isDenied(permission.ACTIVITY_RECOGNITION)) requestList += permission.ACTIVITY_RECOGNITION

        if (isDenied(permission.CAMERA)) requestList += permission.CAMERA

        if (isDenied(permission.ACCESS_FINE_LOCATION)) requestList += permission.ACCESS_FINE_LOCATION

        activityResultLauncher.launch(requestList)
    }

    val contract = ActivityResultContracts.RequestMultiplePermissions()

    private lateinit var activityResultLauncher: ActivityResultLauncher<Array<String>>

    private fun isDenied(name: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            name
        ) != PackageManager.PERMISSION_GRANTED
    }

    private fun makePermissionToText(permissions: Array<String>): String {
        var result = mutableListOf<String>()

        permissions.forEach { permission ->
            when (permission) {
                Manifest.permission.CAMERA -> {
                    result += "카메라 권한"
                }

                Manifest.permission.ACCESS_FINE_LOCATION -> {
                    result += "위치 권한"
                }

                Manifest.permission.ACTIVITY_RECOGNITION -> {
                    result += "신체활동 권한"
                }
            }
        }
        return result.joinToString()
    }

    private fun setUid() {
        lifecycleScope.launch {
            viewModel.setUserInfo()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}