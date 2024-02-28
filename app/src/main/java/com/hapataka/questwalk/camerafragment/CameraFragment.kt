package com.hapataka.questwalk.camerafragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
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
import com.google.android.material.snackbar.Snackbar
import com.hapataka.questwalk.databinding.FragmentCameraBinding

class CameraFragment : Fragment() {


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
    private var cameraDevice: CameraDevice? = null
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
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraViewModel.bitmap.observe(viewLifecycleOwner){

            val capturedImageDialog = CapturedImageDialog()
            capturedImageDialog.show(childFragmentManager, "capturedImage")
        }

        checkPermissions()
        binding.ibCapture.setOnClickListener {
            captureRequestBuilder =
                cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureRequestBuilder.addTarget(imageReader.surface)
            cameraCaptureSession.capture(captureRequestBuilder.build(), null, null)
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

        imageReader = ImageReader.newInstance(960, 1280, ImageFormat.JPEG, 1)
        imageReader.setOnImageAvailableListener({ reader ->
            val image = reader.acquireNextImage()

            //image to byteArray()
            val buffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)

            //decodeByteArray Bitmap
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            cameraViewModel.setBitmap(bitmap)

            image?.close()
            Toast.makeText(requireContext(), "image captured", Toast.LENGTH_SHORT)
                .show()
        }, handler)

    }

    @SuppressLint("MissingPermission", "NewApi")
    private fun openCamera() {
        cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                cameraDevice = camera
                //카메라가 열리면 CqptureSession 생성
                surface = Surface(ttvPreview.surfaceTexture)
                captureRequestBuilder =
                    cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                        addTarget(surface)
                    }
                cameraDevice!!.createCaptureSession(
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
                camera.close()
                cameraDevice = null
                handlerThread.quitSafely()
            }

            override fun onError(camera: CameraDevice, error: Int) {
                camera.close()
                cameraDevice = null
                handlerThread.quitSafely()
            }
        }, handler)
    }
}