package com.hapataka.questwalk

import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.hapataka.questwalk.databinding.FragmentResultBinding


class ResultFragment: Fragment(), OnMapReadyCallback {
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationListener: LocationListener
    private lateinit var locationManager: LocationManager
    lateinit var locationCallback: LocationCallback

    lateinit var locationPermission: ActivityResultLauncher<Array<String>>

    private lateinit var polyline: Polyline
    private lateinit var polylinePoints: List<LatLng>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {

        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                } else -> {
                // No location access granted.
                }
            }
        }

        // Before you perform the actual permission request, check whether your app
        // already has the permissions, and whether your app needs to show a permission
        // rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION))

        polylinePoints = ArrayList()
    }

    override fun onMapReady(p0: GoogleMap) {
        val seoul = LatLng(37.566610, 126.978403)
        googleMap = p0
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL // default 노말 생략 가능
        googleMap.apply {
            val markerOptions = MarkerOptions()
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            markerOptions.position(seoul)
            markerOptions.title("서울시청")
            markerOptions.snippet("Tel:01-120")
            addMarker(markerOptions)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())
        updateLocation()
    }

    fun updateLocation(){

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()

        locationCallback = object : LocationCallback(){
            //1초에 한번씩 변경된 위치 정보가 onLocationResult 으로 전달된다.
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult?.let{
                    for (location in it.locations){
                        Log.d("위치정보",  "위도: ${location.latitude} 경도: ${location.longitude}")
                        setLastLocation(location) //계속 실시간으로 위치를 받아오고 있기 때문에 맵을 확대해도 다시 줄어든다.
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

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback,
            Looper.myLooper()
        )
    }

    fun setLastLocation(lastLocation: Location){
        val LATLNG = LatLng(lastLocation.latitude,lastLocation.longitude)

        val makerOptions = MarkerOptions().position(LATLNG).title("나 여기 있어용~")
        val cameraPosition = CameraPosition.Builder().target(LATLNG).zoom(15.0f).build()

        googleMap.addMarker(makerOptions)
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }
}