package com.hapataka.questwalk.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.widget.Chronometer.OnChronometerTickListener
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
import com.google.android.material.snackbar.Snackbar
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentHomeBinding
import com.hapataka.questwalk.ui.camera.CameraViewModel
import com.hapataka.questwalk.domain.entity.HistoryEntity
import com.hapataka.questwalk.util.BaseFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate), SensorEventListener {
    private val viewModel: HomeViewModel by activityViewModels()
    private val cameraViewModel: CameraViewModel by activityViewModels()
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private var backPressedOnce = false

    private val sensorManager by lazy {
        requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private val sensor: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationPermission: ActivityResultLauncher<Array<String>>
    private var locationHistory: ArrayList<Location> = arrayListOf()

    private var totalDistance: Float = 0.0F
    private var totalSteps: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setObserver()
        getItems()
    }

    private fun initView() {
        initBackground()
        setQuestState()
        initNaviButtons()
        setQuestButtonEvent()
        initBackPressedCallback()
        setStepSensor()
        initLocation()
    }

    private fun setObserver() {
        viewModel.currentKeyword.observe(viewLifecycleOwner) {
            binding.tvQuestKeyword.text = it
        }

        cameraViewModel.isSucceed.observe(viewLifecycleOwner) { isSucceed ->
            if (isSucceed == null) return@observe
            if (isSucceed) {
                Snackbar.make(requireView(), "퀘스트 성공!", Snackbar.LENGTH_SHORT).show()
                cameraViewModel.initIsSucceed()
                viewModel.isQuestSuccess = true
                setQuestState()
            }
        }
    }

    private fun getItems() {
        viewModel.getRandomKeyword()
    }

    private fun initBackground() {
        with(binding) {
            ivBgLayer1.load(R.drawable.background_day_layer1)
            ivBgLayer2.load(R.drawable.background_night_layer2)
            ivBgLayer3.load(R.drawable.background_night_layer3)
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
            viewModel.isPlay = !viewModel.isPlay

            setQuestState()
            if (viewModel.isPlay) {
                updateLocation()
            } else {
                finishLocationClient()
                locationHistory.clear()
                navController.navigate(R.id.action_frag_home_to_frag_result)
            }
        }
    }

    private fun setQuestState() {
        with(binding) {
            if (viewModel.isPlay) { // 모험 시작!

                ibCamera.visibility = View.VISIBLE
                llPlayingContents.visibility = View.VISIBLE
                btnQuestChange.visibility = View.GONE

                if (viewModel.isQuestSuccess) {
                    btnQuestStatus.text = "완료하기"
                    setBackgroundWidget(btnQuestStatus, R.color.green)
                } else {
                    //버튼색 이름 및 색깔 변경
                    btnQuestStatus.text = "포기하기"
                    setBackgroundWidget(btnQuestStatus, R.color.red)
                }
                binding.cmQuestTime.onChronometerTickListener =
                    OnChronometerTickListener { chronometer ->
                        val time = SystemClock.elapsedRealtime() - chronometer.base
                        val h = (time / 3600000).toInt()
                        val m = (time - h * 3600000).toInt() / 60000
                        val s = (time - h * 3600000 - m * 3600000 * 3600000).toInt() / 1000
                        val t =
                            (if (h < 10) "0$h" else h).toString() + ":" + (if (m < 10) "0$m" else m).toString() + ":" + (if (s < 10) "0$s" else s).toString()
                        chronometer.text = t
                    }
                binding.cmQuestTime.base = SystemClock.elapsedRealtime()
                binding.cmQuestTime.text = "00:00:00"
                binding.cmQuestTime.start()
                totalSteps = 0
                totalDistance = 0F

                //버튼색 이름 및 색깔 변경
                btnQuestStatus.text = "포기하기"
                setBackgroundWidget(btnQuestStatus, R.color.red)
                initQuestStart()

            } else { // 모험이 끝날때!
                ibCamera.visibility = View.GONE
                llPlayingContents.visibility = View.GONE
                btnQuestChange.visibility = View.VISIBLE

                binding.cmQuestTime.stop()
                testResults()

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
        with(binding) {
            ivBgLayer1.translationX = 0f
            ivBgLayer2.translationX = 0f
            ivBgLayer3.translationX = 0f
            ivBgLayer1.startAnimation(setAnimator(0.25f, -0.25f, 10000))
            ivBgLayer2.startAnimation(setAnimator(0.7f, -0.7f, 40000))
            ivBgLayer3.startAnimation(setAnimator(0.25f, -0.25f, 80000))
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

    private fun initQuestEnd() {
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

    private fun setStepSensor() {
        if (sensor == null) {
            Toast.makeText(
                this.requireContext(),
                "No sensor detected on this device",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val sensor = event!!.sensor

        if (sensor.type == Sensor.TYPE_STEP_DETECTOR) {
            totalSteps++
            binding.tvQuestPlaying.text = "%d걸음".format(totalSteps)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun initLocation() {
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireContext())
    }

    private fun updateLocation() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
        var preLocation: Location? = null

        locationCallback = object : LocationCallback() {
            //1초에 한번씩 변경된 위치 정보가 onLocationResult 으로 전달된다.
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.let {
                    for (location in it.locations) {
                        Log.d("loc", "현위치 %s, %s".format(location.latitude, location.longitude))
                        if (location.hasAccuracy() && (location.accuracy <= 30) && (preLocation != null)) {
                            if (location.accuracy * 1.5 < location.distanceTo(preLocation!!)) {
                                totalDistance += location.distanceTo(preLocation!!)
                                binding.tvQuestDistance.text = "%.1f km".format(totalDistance)
                                preLocation = location
                                locationHistory.add(location)
                            }
                        } else {
                            locationHistory.add(location)
                            preLocation = location
                        }
                    }
                }
            }
        }
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
            locationRequest, locationCallback,
            Looper.myLooper()!!
        )
    }

    private fun finishLocationClient() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun testResults() {
        var result = HistoryEntity.ResultEntity(
            quest = binding.tvQuestKeyword.text.toString(),
            time = binding.cmQuestTime.text.toString(),
            distance = totalDistance,
            step = totalSteps,
            latitueds = locationHistory.map { it.latitude.toFloat() },
            longitudes = locationHistory.map { it.longitude.toFloat() },
            questLatitued = locationHistory.lastOrNull()?.latitude?.toFloat() ?: 0F,
            questLongitude = locationHistory.lastOrNull()?.longitude?.toFloat() ?: 0F
        )
        Log.d("result", result.toString())


    }
    override fun onResume() {
        super.onResume()
        setQuestState()
    }
}