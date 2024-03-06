package com.hapataka.questwalk

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.hapataka.questwalk.databinding.FragmentResultBinding
import java.util.Timer
import java.util.TimerTask


class ResultFragment : Fragment(), OnMapReadyCallback, SensorEventListener {
    private var totalSteps: Float = 0.0f
    private var tCount: Long = 0
    private lateinit var advTimer: Timer
    private var _binding : FragmentResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var mapView: MapView

    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback
    private lateinit var locationPermission: ActivityResultLauncher<Array<String>>

    private val sensorManager by lazy {
        this.context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private val sensor: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationPermission = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                }
                permissions.getOrDefault(ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                } else -> {
                // No location access granted.
            }
            }
        }

        //권한 요청
        locationPermission.launch(
            arrayOf(
                ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION
            )
        )

        if (sensor == null) {
            // This will give a toast message to the user if there is no sensor in the device
            Toast.makeText(this.requireContext(), "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            // Rate suitable for the user interface
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResultBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView(savedInstanceState)
        setTime()
    }

    private fun setTime() {
        advTimer = Timer()
        advTimer.schedule(object :TimerTask(){
            override fun run() {
                val mHandler = Handler(Looper.getMainLooper())
                mHandler.postDelayed({
                    binding.tvAdvTime.text="%d시간 %d분".format(tCount /60, tCount %60)
                    tCount++
                }, 0)
            }
        },0,60000)
    }

    private fun initView(savedInstanceState: Bundle?) {
        setMap(savedInstanceState)
        initText()
    }

    private fun initText() {
        binding.tvAdvTime.text= getString(R.string.default_time)
        binding.tvAdvDistance.text= getString(R.string.default_distance)
        binding.tvTotalSteps.text= getString(R.string.default_total_steps)
        binding.tvCalories.text= getString(R.string.default_calories)
    }

    private fun setMap(savedInstanceState: Bundle?) {
        mapView=binding.fragMap
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMapReady(p0: GoogleMap) {
        MapsInitializer.initialize(this.requireContext())

        // Updates the location and zoom of the MapView
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(35.141233, 126.925594), 14f)
//
//        p0.animateCamera(cameraUpdate)
//
//        p0.addMarker(
//            MarkerOptions()
//                .position(LatLng(35.141233, 126.925594))
//                .title("루프리코리아")
//        )

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireContext())
        updateLocation(p0)
    }

    private fun updateLocation(p0: GoogleMap) {

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
        var preLocation : Location? = null

        locationCallback = object : LocationCallback(){
            //1초에 한번씩 변경된 위치 정보가 onLocationResult 으로 전달된다.
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.let{
                    for (location in it.locations){
                        Log.d("위치정보",  "위도: ${location.latitude} 경도: ${location.longitude}")
                        setLastLocation(p0, location) //계속 실시간으로 위치를 받아오고 있기 때문에 맵을 확대해도 다시 줄어든다.
                        if(preLocation!=null){
                            var polyline = p0.addPolyline(
                                PolylineOptions()
                                    .clickable(true)
                                    .add(
                                        LatLng(preLocation!!.latitude, preLocation!!.longitude),
                                        LatLng(location.latitude, location.longitude)
                                    )
                            )
                            polyline.width = 15.toFloat()
                            polyline.color = -0xa80e9
                            polyline.jointType = JointType.ROUND
                            polyline.startCap=RoundCap()
                            polyline.endCap=RoundCap()
                        }
                        preLocation=location
                    }
                }
            }
        }
        //권한 처리
        if (ActivityCompat.checkSelfPermission(
                this.requireContext(),
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this.requireContext(),
                ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback,
            Looper.myLooper()!!
        )
    }

    fun setLastLocation(p0: GoogleMap, lastLocation: Location){
        val LATLNG = LatLng(lastLocation.latitude,lastLocation.longitude)

        val makerOptions = MarkerOptions().position(LATLNG).title("나 여기 있어용~")
        val cameraPosition = CameraPosition.Builder().target(LATLNG).zoom(150.0f).build()

//        p0.addMarker(makerOptions)
        p0.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    override fun onSensorChanged(event: SensorEvent?) {
        totalSteps = event!!.values[0]
        binding.tvTotalSteps.text = "%d걸음".format(totalSteps.toInt())
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }


}