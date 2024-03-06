package com.hapataka.questwalk.ui.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.PixelFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.ImageReader
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentCameraBinding
import dagger.hilt.android.AndroidEntryPoint
import java.nio.ByteBuffer
import java.nio.IntBuffer


@AndroidEntryPoint
class CameraFragment : Fragment() {


    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }

    private val cameraViewModel: CameraViewModel by activityViewModels()
    private lateinit var binding: FragmentCameraBinding

    // permission 등록 콜백 함수
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

    private lateinit var handler: Handler
    private lateinit var handlerThread: HandlerThread

    private lateinit var captureRequestBuilder: CaptureRequest.Builder
    private lateinit var cameraCaptureSession: CameraCaptureSession


    private lateinit var imageReader: ImageReader


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        ttvPreview = binding.ttvPreview
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraViewModel.bitmap.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            //캡쳐 성공시 CaptureFragment 이동
            navController.navigate(R.id.action_frag_camera_to_frag_capture)
        }

        checkPermissions()

        bindingImageButton()


    }

    private fun bindingImageButton() {
        with(binding) {
            ibCapture.setOnClickListener {
                captureRequestBuilder =
                    cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
                captureRequestBuilder.addTarget(imageReader.surface)
                cameraCaptureSession.capture(captureRequestBuilder.build(), null, null)
            }
            ibFlash.setOnClickListener {
                // TODO : 플래시 ON/OFF
            }
            ibBackBtn.setOnClickListener {
                navController.popBackStack()
            }
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

        handlerThread = HandlerThread("previewThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)

        cameraManager = requireActivity().getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = cameraManager.cameraIdList[0]
        //camera getData
        getCharacterCamera()
        setImageReader()
        ttvPreview.post{
            openCamera()
        }


    }

    private fun setImageReader() {
        val maxSize = cameraViewModel.getCameraMaxSize()
        imageReader = ImageReader.newInstance(
            maxSize.width,
            maxSize.height,
            PixelFormat.RGBA_8888,
            1
        )
        imageReader.setOnImageAvailableListener({ reader ->
            val image = reader.acquireNextImage()

            //image to byteArray()
            val buffer = image.planes[0].buffer
            val pixelValues = IntArray(buffer.capacity()/4)
            buffer.asIntBuffer().get(pixelValues)
            //decodeByteArray Bitmap
            val bitmap = Bitmap.createBitmap(image.width,image.height,Bitmap.Config.ARGB_8888)
            bitmap.copyPixelsFromBuffer(IntBuffer.wrap(pixelValues))
            cameraViewModel.setBitmap(bitmap)

            image?.close()
            Toast.makeText(requireContext(), "image captured", Toast.LENGTH_SHORT)
                .show()
        }, handler)
    }

    private fun getCharacterCamera() {
        //rotation ,sizes -> ViewModel 에 전달하고 처리하자
        val map: StreamConfigurationMap?
        val rotation: Float
        cameraManager.apply {
            rotation =
                getCameraCharacteristics(cameraId).get(CameraCharacteristics.SENSOR_ORIENTATION)!!
                    .toFloat()
            map =
                getCameraCharacteristics(cameraId).get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        }

        cameraViewModel.setCameraCharacteristics(rotation, map?.getOutputSizes(PixelFormat.RGBA_8888)!!)
    }

    @SuppressLint("MissingPermission", "NewApi")
    private fun openCamera() {
        cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                cameraDevice = camera
                //카메라가 열리면 CqptureSession 생성
                surface = Surface(ttvPreview.surfaceTexture)
                captureRequestBuilder =
                    cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                        addTarget(surface)
                    }
                cameraDevice.createCaptureSession(
                    listOf(surface, imageReader.surface),
                    object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(session: CameraCaptureSession) {
                            cameraCaptureSession = session.apply {
                                setRepeatingRequest(captureRequestBuilder.build(), null, null)
                            }

                        }

                        override fun onConfigureFailed(session: CameraCaptureSession) {
                        }
                    },
                    handler
                )
            }

            override fun onDisconnected(camera: CameraDevice) {
            }

            override fun onError(camera: CameraDevice, error: Int) {
            }
        }, handler)
    }

    override fun onPause() {
        super.onPause()
        try {
            cameraCaptureSession.close()
            cameraDevice.close()
            handlerThread.quitSafely()
        } catch (_: Exception) {

        }

    }
}