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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.updateBounds
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.R
import com.hapataka.questwalk.core.designsystem.component.ImageButton
import com.hapataka.questwalk.core.designsystem.component.PixelChipButton
import com.hapataka.questwalk.core.designsystem.theme.Black
import com.hapataka.questwalk.core.designsystem.theme.SystemCyan
import com.hapataka.questwalk.core.designsystem.theme.SystemGray
import com.hapataka.questwalk.core.designsystem.theme.SystemLemon
import com.hapataka.questwalk.core.designsystem.theme.Typography
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
import com.hapataka.questwalk.ui.result.RESULT_ID
import com.hapataka.questwalk.util.extentions.SIMPLE_TIME
import com.hapataka.questwalk.util.extentions.convertKm
import com.hapataka.questwalk.util.extentions.convertTime
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: HomeViewModel by viewModels()
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private var backPressedOnce = false
    private val sensorManager by lazy {
        requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private var currentDistance = 0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.setRandomKeyword()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadInitialSetting()
        initComposeView()
        checkPermissions()
        setup()
    }

    private fun loadInitialSetting() {
        setObserver()
    }

    private fun setup() {
        initBackPressedCallback()
        setUid()
    }

    private fun initComposeView() {
        binding.cvBg.setContent {
            val currentKeyword by mainViewModel.currentKeyword.observeAsState()
            val keywordLevel by mainViewModel.keywordLevel.collectAsStateWithLifecycle()
            val playState by mainViewModel.playState.observeAsState()
            val currentTime by viewModel.timeState.collectAsStateWithLifecycle()
            var animState by remember { mutableStateOf(false) }

            LaunchedEffect(playState) {
                animState = playState != QUEST_STOP
            }

            val localDensity = LocalDensity.current
            val navigationBarHeight = with(localDensity) {
                requireContext().navigationHeight().toDp()
            }
            val statusBarHeight = with(localDensity) {
                requireContext().statusBarHeight().toDp()
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = SystemGray)
                    .padding(bottom = navigationBarHeight)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.7f)
                        .fillMaxWidth(),
                ) {
                    HorizontalScrollingBackground(
                        isAnimate = animState,
                        modifier = Modifier
                            .fillMaxSize(),
                        imgId = when (currentTime) {
                            in 7..18 -> drawable.bg_landscape_day
                            else -> drawable.bg_landscape_night
                        },
                        duration = 160000
                    )
                    HorizontalScrollingBackground(
                        isAnimate = animState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.5f)
                            .align(Alignment.TopCenter)
                            .padding(top = 80.dp),
                        imgId = when (currentTime) {
                            in 7..18 -> R.drawable.background_day_layer2
                            else -> drawable.bg_starts
                        },
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
                            .fillMaxHeight(0.42f)
                            .padding(bottom = 72.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(top = 16.dp, end = 12.dp)
                                .width(64.dp)
                                .padding(top = statusBarHeight)
                                .align(Alignment.TopEnd),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(-2.dp),
                        ) {
                            ImageButton(
                                releasedPainterResource = painterResource(R.drawable.btn_weather),
                                contentDescription = "날씨 버튼",
                                modifier = Modifier
                                    .size(64.dp),
                                onClick = { navController.navigate(R.id.action_frag_home_to_weatherFragment) }
                            )
                            Image(
                                painter = painterResource(R.drawable.image_chain),
                                contentDescription = null,
                            )
                            ImageButton(
                                releasedPainterResource = painterResource(R.drawable.btn_my_info),
                                contentDescription = "내 정보 버튼",
                                modifier = Modifier
                                    .size(52.dp),
                                onClick = { navController.navigate(R.id.action_frag_home_to_frag_my_info) }
                            )
                            Image(
                                painter = painterResource(R.drawable.image_chain),
                                contentDescription = null,
                            )
                            ImageButton(
                                releasedPainterResource = painterResource(R.drawable.btn_history),
                                contentDescription = "히스토리 버튼",
                                modifier = Modifier
                                    .size(52.dp),
                                onClick = { navController.navigate(R.id.action_frag_home_to_frag_record) }
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.TopCenter)
                                .padding(top = 100.dp + statusBarHeight),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(
                                    4.dp,
                                    Alignment.CenterHorizontally
                                )
                            ) {
                                repeat(keywordLevel) {
                                    Image(
                                        painter = painterResource(R.drawable.ic_level_star),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            }

                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentKeyword ?: "",
                                    style = Typography.displayMedium.copy(
                                        drawStyle = Stroke(
                                            width = 16f,
                                            miter = 16f,
                                            cap = StrokeCap.Round,
                                        )
                                    ),
                                    color = Black,
                                )
                                Text(
                                    text = currentKeyword ?: "",
                                    style = Typography.displayMedium,
                                    color = Color.White,
                                )
                            }

                            if (playState == QUEST_STOP) {
                                PixelChipButton(
                                    text = "퀘스트 변경",
                                    onClick = { navController.navigate(R.id.action_frag_home_to_frag_quest) }
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    val context = LocalContext.current
                    val bg = ContextCompat
                        .getDrawable(
                            context,
                            R.drawable.bg_info_bar
                        )

                    Image(
                        modifier = Modifier
                            .fillMaxWidth(),
                        painter = painterResource(R.drawable.image_gradiant_dividor),
                        contentDescription = null,
                        contentScale = ContentScale.FillHeight
                    )

                    val duration by mainViewModel.durationTime.observeAsState()
                    val distance by mainViewModel.totalDistance.observeAsState()
                    val step by mainViewModel.totalStep.observeAsState()

                    LaunchedEffect(distance) {
                        currentDistance = distance ?: -1f
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .height(56.dp)
                            .drawBehind {
                                bg?.updateBounds(0, 0, size.width.toInt(), size.height.toInt())
                                bg?.draw(drawContext.canvas.nativeCanvas)
                            }
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        if (playState == QUEST_STOP) {
                            Text(
                                text = "시작하려면, START버튼을 눌러주세요",
                                style = Typography.bodyMedium,
                                color = SystemGray
                            )
                        } else {
                            Text(
                                text = duration?.convertTime(SIMPLE_TIME) ?: "",
                                style = Typography.bodyMedium,
                                color = Color.White
                            )

                            Text(
                                text = "${step ?: "0"} 걸음",
                                style = Typography.bodyMedium,
                                color = SystemCyan
                            )

                            Text(
                                text = distance?.convertKm() ?: "0m",
                                style = Typography.bodyMedium,
                                color = SystemLemon
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (playState) {
                        QUEST_STOP -> {
                            StartButton(
                                modifier = Modifier
                                    .fillMaxWidth(0.35f),
                                onClick = { mainViewModel.togglePlay() }
                            )
                        }

                        QUEST_START -> {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.spacedBy(
                                    32.dp,
                                    Alignment.CenterHorizontally
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ImageButton(
                                    releasedPainterResource = painterResource(R.drawable.btn_stop_fail),
                                    contentDescription = "퀘스트 포기 버튼",
                                    modifier = Modifier
                                        .fillMaxHeight(0.65f)
                                        .aspectRatio(1f),
                                    onClick = { mainViewModel.togglePlay() }
                                )

                                ImageButton(
                                    releasedPainterResource = painterResource(R.drawable.btn_camera),
                                    contentDescription = "촬영 하기 버튼",
                                    modifier = Modifier
                                        .fillMaxHeight(0.65f)
                                        .aspectRatio(1f),
                                    onClick = { navController.navigate(R.id.action_frag_home_to_frag_camera) }
                                )
                            }
                        }

                        QUEST_SUCCESS -> {
                            ImageButton(
                                releasedPainterResource = painterResource(R.drawable.btn_stop_success),
                                contentDescription = "완료하기 버튼",
                                modifier = Modifier
                                    .fillMaxHeight(0.65f)
                                    .aspectRatio(2f),
                                onClick = { mainViewModel.togglePlay() },
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun StartButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val buttonBackground = painterResource(
            if (isPressed) R.drawable.btn_start_press else R.drawable.btn_start_defualt
        )

        Box(
            modifier = modifier
                .clickable(
                    onClick = onClick,
                    indication = null,
                    interactionSource = interactionSource
                ),
        ) {
            Image(
                painter = buttonBackground,
                contentDescription = "퀘스트 시작 버튼",
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }

    private fun setObserver() {
        with(mainViewModel) {
            playState.observe(viewLifecycleOwner) { state ->
                updateWithState(state)
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
            mainViewModel.stopPlay { resultId ->
                val bundle = Bundle().apply {
                    putString(RESULT_ID, resultId)
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
        if (playState == QUEST_SUCCESS) {
            mainViewModel.setSnackBarMsg("퀘스트 성공!")
            return
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

    private val contract = ActivityResultContracts.RequestMultiplePermissions()

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
}