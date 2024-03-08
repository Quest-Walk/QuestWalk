package com.hapataka.questwalk.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Looper
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentHomeBinding
import com.hapataka.questwalk.ui.camera.CameraViewModel
import com.hapataka.questwalk.ui.result.QUEST_KEYWORD
import com.hapataka.questwalk.ui.result.REGISTER_TIME
import com.hapataka.questwalk.ui.result.USER_ID
import com.hapataka.questwalk.util.BaseFragment
import com.hapataka.questwalk.util.ViewModelFactory
import com.hapataka.questwalk.util.extentions.SIMPLE_TIME
import com.hapataka.questwalk.util.extentions.convertTime
import com.hapataka.questwalk.util.extentions.gone
import com.hapataka.questwalk.util.extentions.invisible
import com.hapataka.questwalk.util.extentions.visible
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private val viewModel: HomeViewModel by activityViewModels { ViewModelFactory() }
    private val cameraViewModel: CameraViewModel by activityViewModels()
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private var backPressedOnce = false

    private val sensorManager by lazy {
        requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationPermission: ActivityResultLauncher<Array<String>>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setObserver()
        getItems()
    }

    private fun initView() {
        initBackground()
        initNaviButtons()
        initQuestButton()
        initBackPressedCallback()
        checkPermission()
        setLocationClient()

        binding.btnWheather.setOnClickListener {
            if (viewModel.isNight.value!!) {
                viewModel.time = 11
            } else {
                viewModel.time = 23
            }
            viewModel.checkCurrentTime()
        }
    }

    private fun checkPermission() {
        locationPermission = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                }

                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                }

                else -> {
                    // No location access granted.
                }
            }
        }

        //권한 요청
        locationPermission.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }


    private fun setObserver() {
        with(viewModel) {
            currentKeyword.observe(viewLifecycleOwner) {
                binding.tvQuestKeyword.text = it
            }
            isPlay.observe(viewLifecycleOwner) {
                toggleViews(it)
                toggleLocation(it)
            }
            durationTime.observe(viewLifecycleOwner) {
                binding.tvQuestTime.text = it.convertTime(SIMPLE_TIME)
            }
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
            totalDistance.observe(viewLifecycleOwner) {
                binding.tvQuestDistance.text = "%.1f km".format(it)
            }
        }

        cameraViewModel.isSucceed.observe(viewLifecycleOwner) { isSucceed ->
            if (isSucceed == null) return@observe
            if (isSucceed) {
                Snackbar.make(requireView(), "퀘스트 성공!", Snackbar.LENGTH_SHORT).show()

                cameraViewModel.initIsSucceed()
                with(binding) {
                    setBackgroundWidget(btnToggleQuestState, R.color.green)
                    btnToggleQuestState.text = "완료하기"
                }
                viewModel.setQuestSuccessLocation()
            }
        }
    }

    private fun getItems() {
        viewModel.checkCurrentTime()
        viewModel.getRandomKeyword()
    }

    private fun initBackground() {
        with(binding) {
            ivBgLayer1.load(R.drawable.background_day_layer1)
            ivBgLayer1.translationX = 2115f
            ivBgLayer2.translationX = -2800f
            ivBgLayer3.translationX = 2115f
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
//            btnWheather.setOnClickListener {
//                // TODO : wheatherFragment 이동
//            }
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
            viewModel.toggleIsPlay {uid, keyword, registerAt ->
                val bundle = Bundle().apply {
                    putString(USER_ID,uid)
                    putString(QUEST_KEYWORD,keyword)
                    putString(REGISTER_TIME, registerAt)
                }
                navController.navigate(R.id.action_frag_home_to_frag_result, bundle)
                finishLocationClient()
//                viewModel.updateUserInfo()
            }
        }
    }

    private fun toggleViews(isPlay: Boolean) {
        with(binding) {
            if (isPlay) {
                ibCamera.visible()
                llPlayingContents.visible()
                tvQuestChange.invisible()
                btnToggleQuestState.text = "포기하기"
                setBackgroundWidget(btnToggleQuestState, R.color.red)
                startBackgroundAnim()
            } else {
                ibCamera.gone()
                llPlayingContents.gone()
                tvQuestChange.visible()
                btnToggleQuestState.text = "모험 시작하기"
                setBackgroundWidget(btnToggleQuestState, R.color.button)
                endBackgroundAnim()
            }
        }
    }

    private fun toggleLocation(isPlay: Boolean) {
        if (isPlay) {
            updateLocation()
            initStepSensor()
        } else {
            sensorManager.unregisterListener(sensorListener, stepSensor)
        }
    }

    private fun startBackgroundAnim() {
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
        with(binding) {
            ivBgLayer1.translationX = 0f
            ivBgLayer2.translationX = 0f
            ivBgLayer3.translationX = 0f
            ivBgLayer1.startAnimation(setAnimator(0.25f, -0.25f, 10000))
            ivBgLayer2.startAnimation(setAnimator(0.7f, -0.7f, 40000))
            ivBgLayer3.startAnimation(setAnimator(0.25f, -0.25f, 80000))
        }
    }

    private fun endBackgroundAnim() {
        with(binding) {
            ivChrImage.load(R.drawable.character_01)
            ivBgLayer1.clearAnimation()
            ivBgLayer2.clearAnimation()
            ivBgLayer3.clearAnimation()
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
    }

    private val sensorListener by lazy { makeSensorListener() }
    private val stepSensor by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) }

    private fun makeSensorListener(): SensorEventListener {
        return object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                val sensor = event!!.sensor

                if (sensor.type == Sensor.TYPE_STEP_DETECTOR) {
                    viewModel.updateStep()
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    private fun updateLocation() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000).build()

        locationCallback = object : LocationCallback() {
            //1초에 한번씩 변경된 위치 정보가 onLocationResult 으로 전달된다.
            override fun onLocationResult(locationResult: LocationResult) {
                viewModel.updateLocation(locationResult)
            }
        }
        requestLocationClient(locationRequest)
    }

    private fun setLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireContext())
    }

    private fun finishLocationClient() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun requestLocationClient(locationRequest: LocationRequest) {
        //권한 처리
        if (ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()!!
        )
    }

    private fun String.showToast() {
        Toast.makeText(requireContext(), this, Toast.LENGTH_SHORT).show()
    }
}