package com.hapataka.questwalk.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.R
import com.hapataka.questwalk.core.designsystem.theme.QuestWalkTheme
import com.hapataka.questwalk.feature.home.HomeRoute
import com.hapataka.questwalk.feature.home.HomeUiState
import com.hapataka.questwalk.feature.home.HomeViewModel
import com.hapataka.questwalk.feature.home.QUEST_START
import com.hapataka.questwalk.feature.home.QUEST_STOP
import com.hapataka.questwalk.feature.home.QUEST_SUCCESS
import com.hapataka.questwalk.feature.home.component.PermissionDialog
import com.hapataka.questwalk.feature.home.component.StopPlayDialog
import com.hapataka.questwalk.ui.main.MainViewModel
import com.hapataka.questwalk.ui.result.RESULT_ID
import com.hapataka.questwalk.util.extentions.SIMPLE_TIME
import com.hapataka.questwalk.util.extentions.convertKm
import com.hapataka.questwalk.util.extentions.convertTime
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: HomeViewModel by viewModels()
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private var backPressedOnce = false
    private val sensorManager by lazy {
        requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.setRandomKeyword()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireActivity()).apply {
            setContent {
                val currentKeyword by mainViewModel.currentKeyword.observeAsState("")
                val keywordLevel by mainViewModel.keywordLevel.collectAsStateWithLifecycle()
                val playState by mainViewModel.playState.observeAsState(QUEST_STOP)
                val currentTime by viewModel.timeState.collectAsStateWithLifecycle()
                val duration by mainViewModel.durationTime.observeAsState(-1L)
                val distance by mainViewModel.totalDistance.observeAsState(0f)
                val step by mainViewModel.totalStep.observeAsState(0L)
                val isStop by mainViewModel.isStop.observeAsState(false)

                var showStopDialog by remember { mutableStateOf(false) }
                var currentDistance by remember { mutableFloatStateOf(0f) }

                // Permission state
                var showPermissionDialog by remember { mutableStateOf(false) }
                var permissionMessage by remember { mutableStateOf("") }

                // Update distance for dialog
                currentDistance = distance ?: 0f

                // Handle stop dialog trigger
                if (isStop == true && !showStopDialog) {
                    showStopDialog = true
                }

                val localDensity = LocalDensity.current
                val navigationBarHeight = with(localDensity) {
                    requireContext().navigationHeight().toDp()
                }
                val statusBarHeight = with(localDensity) {
                    requireContext().statusBarHeight().toDp()
                }

                val uiState = HomeUiState(
                    currentKeyword = currentKeyword ?: "",
                    keywordLevel = keywordLevel,
                    playState = playState ?: QUEST_STOP,
                    currentTime = currentTime,
                    duration = duration?.convertTime(SIMPLE_TIME) ?: "",
                    step = (step ?: 0).toString(),
                    distance = distance?.convertKm() ?: "0m",
                    statusBarHeight = statusBarHeight,
                    navigationBarHeight = navigationBarHeight,
                )

                QuestWalkTheme(lightBar = false) {
                    HomeRoute(
                        uiState = uiState,
                        onStartClick = { mainViewModel.togglePlay() },
                        onStopClick = { mainViewModel.togglePlay() },
                        onCameraClick = { navController.navigate(R.id.action_frag_home_to_frag_camera) },
                        onCompleteClick = { mainViewModel.togglePlay() },
                        onQuestChangeClick = { navController.navigate(R.id.action_frag_home_to_frag_quest) },
                        onWeatherClick = { navController.navigate(R.id.action_frag_home_to_weatherFragment) },
                        onMyInfoClick = { navController.navigate(R.id.action_frag_home_to_frag_my_info) },
                        onRecordClick = { navController.navigate(R.id.action_frag_home_to_frag_record) },
                    )

                    // Stop Play Dialog
                    if (showStopDialog) {
                        StopPlayDialog(
                            distance = currentDistance,
                            onConfirm = {
                                showStopDialog = false
                                lifecycleScope.launch {
                                    mainViewModel.stopPlay { resultId ->
                                        val bundle = Bundle().apply {
                                            putString(RESULT_ID, resultId)
                                        }
                                        navController.navigate(R.id.action_frag_home_to_frag_result, bundle)
                                    }
                                }
                            },
                            onDismiss = {
                                showStopDialog = false
                                mainViewModel.resumePlay()
                            }
                        )
                    }

                    // Permission Dialog
                    if (showPermissionDialog) {
                        PermissionDialog(
                            message = permissionMessage,
                            onConfirm = {
                                showPermissionDialog = false
                                // Navigate to settings
                                android.content.Intent(
                                    android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    android.net.Uri.fromParts("package", requireActivity().packageName, null)
                                ).also { startActivity(it) }
                            },
                            onDismiss = {
                                showPermissionDialog = false
                                requireActivity().finish()
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        initBackPressedCallback()
        checkPermissions()
        setUid()
    }

    private fun setObserver() {
        with(mainViewModel) {
            playState.observe(viewLifecycleOwner) { state ->
                updateWithState(state)
            }
        }
    }

    private fun updateWithState(playState: Int) {
        updateViews(playState)
        toggleStepSensor(playState)
    }

    private fun updateViews(playState: Int) {
        if (playState == QUEST_SUCCESS) {
            mainViewModel.setSnackBarMsg("퀘스트 성공!")
        }
    }

    private fun toggleStepSensor(playState: Int) {
        if (playState == QUEST_START) {
            initStepSensor()
            return
        }
        if (playState == QUEST_STOP) {
            sensorManager.unregisterListener(sensorListener, stepSensor)
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
            Toast.makeText(requireContext(), "No sensor detected on this device", Toast.LENGTH_SHORT).show()
            return
        }
        sensorManager.registerListener(sensorListener, stepSensor, SensorManager.SENSOR_DELAY_UI)
    }

    private val sensorListener by lazy {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                val sensor = event?.sensor ?: return
                if (sensor.type == Sensor.TYPE_STEP_DETECTOR) {
                    mainViewModel.countUpStep()
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    private val stepSensor by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) }

    private fun checkPermissions() {
        makeResultLauncher()

        var requestList = arrayOf<String>()
        if (isDenied(Manifest.permission.ACTIVITY_RECOGNITION)) {
            requestList += Manifest.permission.ACTIVITY_RECOGNITION
        }
        if (isDenied(Manifest.permission.CAMERA)) {
            requestList += Manifest.permission.CAMERA
        }
        if (isDenied(Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestList += Manifest.permission.ACCESS_FINE_LOCATION
        }
        activityResultLauncher.launch(requestList)
    }

    private val contract = ActivityResultContracts.RequestMultiplePermissions()
    private lateinit var activityResultLauncher: ActivityResultLauncher<Array<String>>

    private fun makeResultLauncher() {
        activityResultLauncher = registerForActivityResult(contract) { permissions ->
            if (permissions.values.all { it }) return@registerForActivityResult

            val deniedPermissions = permissions.filterValues { !it }.keys.toTypedArray()
            val needPermission = makePermissionToText(deniedPermissions)

            // Show permission dialog via Compose state would require more refactoring
            // For now, keeping the basic permission handling
            Toast.makeText(
                requireContext(),
                "퀘스트 워크를 사용하기 위해서는 $needPermission 이 필요합니다",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun isDenied(name: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            name
        ) != PackageManager.PERMISSION_GRANTED
    }

    private fun makePermissionToText(permissions: Array<String>): String {
        val result = mutableListOf<String>()
        permissions.forEach { permission ->
            when (permission) {
                Manifest.permission.CAMERA -> result += "카메라 권한"
                Manifest.permission.ACCESS_FINE_LOCATION -> result += "위치 권한"
                Manifest.permission.ACTIVITY_RECOGNITION -> result += "신체활동 권한"
            }
        }
        return result.joinToString()
    }

    private fun setUid() {
        lifecycleScope.launch {
            // User info setup is handled in the old HomeViewModel
            // which is still used for time state
        }
    }

    private fun Context.navigationHeight(): Int {
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
    }

    private fun Context.statusBarHeight(): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
    }
}
