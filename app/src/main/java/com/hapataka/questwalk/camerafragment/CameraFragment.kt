package com.hapataka.questwalk.camerafragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.hapataka.questwalk.databinding.FragmentCameraBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {


    private lateinit var viewModel: CameraViewModel
    private lateinit var binding: FragmentCameraBinding

    // permission 등록 콜백함수
    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                setCamera()
            } else {
                Snackbar.make(requireView(), "권한 받아오기 실패", Snackbar.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            }
        }


    // 카메라 관련 변수
    private lateinit var ttvPreview: TextureView
    private lateinit var surface: Surface

    private lateinit var cameraManager: CameraManager
    private lateinit var cameraDevice: CameraDevice
    private var cameraId: String = ""

    private lateinit var executor: ExecutorService

    private lateinit var cameraCaptureSession: CameraCaptureSession

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[CameraViewModel::class.java]
        // TODO: Use the ViewModel
        checkPermissions()
        binding.ibCapture.setOnClickListener {
            val captureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE).apply {
                addTarget(surface)
            }.build()
            cameraCaptureSession.captureSingleRequest(captureRequest,executor,object : CameraCaptureSession.CaptureCallback(){})
            //이후 캡쳐된 이미지를 가져올려면 콜백함수를 정의해야함 ㅇㅇ
        }
    }

    private fun checkPermissions() {
        if (
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            setCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun setCamera() {
        executor = Executors.newSingleThreadExecutor()
        ttvPreview = binding.ttvPreview
        ttvPreview.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int,
            ) {
                openCamera()
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int,
            ) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
            }

        }

        cameraManager = requireActivity().getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = cameraManager.cameraIdList[0]

    }

    @SuppressLint("MissingPermission", "NewApi")
    private fun openCamera() {
        cameraManager.openCamera(cameraId, executor, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                cameraDevice = camera
                //카메라가 열리면 CqptureSession 생성
                surface = Surface(ttvPreview.surfaceTexture)
                val outputConfig = OutputConfiguration(surface)
                val sessionConfig = SessionConfiguration(
                    SessionConfiguration.SESSION_REGULAR,
                    listOf(outputConfig),
                    executor,
                    object : CameraCaptureSession.StateCallback(){
                        override fun onConfigured(session: CameraCaptureSession) {
                            //세션이 구성되면 프리뷰 시작
                            val previewRequest = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply{
                                addTarget(surface)
                            }.build()

                            cameraCaptureSession = session
                            session.setSingleRepeatingRequest(previewRequest,executor,object : CameraCaptureSession.CaptureCallback(){})
                        }

                        override fun onConfigureFailed(session: CameraCaptureSession) {
                        }

                    })
                camera.createCaptureSession(sessionConfig)
            }

            override fun onDisconnected(camera: CameraDevice) {
                camera.close()
            }

            override fun onError(camera: CameraDevice, error: Int) {
                camera.close()
            }
        })
    }
}