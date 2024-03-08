package com.hapataka.questwalk.ui.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentCameraBinding
import com.hapataka.questwalk.ui.home.HomeViewModel
import com.hapataka.questwalk.util.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Runnable


@AndroidEntryPoint
class CameraFragment : BaseFragment<FragmentCameraBinding>(FragmentCameraBinding::inflate) {
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private val cameraViewModel: CameraViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()

    // 카메라 관련 변수
    private var imageCapture: ImageCapture? = null

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkPermissions()
        setObserver()
        bindingWidgets()
    }

    /**
     *  Observer 및 ViewBinding
     */

    private fun setObserver() {

        cameraViewModel.bitmap.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            //캡쳐 성공시 CaptureFragment 이동
            navController.navigate(R.id.action_frag_camera_to_frag_capture)
        }
        cameraViewModel.isSucceed.observe(viewLifecycleOwner) { isSucceed ->
            if (isSucceed == null) return@observe
            if (!isSucceed) {
                cameraViewModel.deleteBitmapByFile()
            }
        }
    }

    private fun bindingWidgets() {
        with(binding) {
            ibCapture.apply {
                setOnClickListener {
                    capturePhoto()
                }
                setOnTouchListener(
                    imageButtonSetOnTouchListener(
                        this,
                        R.drawable.ico_camera_capture_on,
                        R.drawable.ico_camera_capture
                    )
                )
            }
            ibFlash.setOnClickListener {
                toggleFlash()
            }
            ibBackBtn.apply {
                setOnClickListener {
                    navController.popBackStack()
                }
                setOnTouchListener(
                    imageButtonSetOnTouchListener(
                        this,
                        R.drawable.ico_camera_back_on,
                        R.drawable.ico_camera_back
                    )
                )
            }
            tvCameraQuest.text = homeViewModel.currentKeyword.value
        }
    }

    private fun imageButtonSetOnTouchListener(
        imageButton: ImageButton,
        actionDownRes: Int,
        actionUpRes: Int,
    ): View.OnTouchListener {
        return View.OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    imageButton.setImageResource(actionDownRes)
                }

                MotionEvent.ACTION_UP -> {
                    imageButton.setImageResource(actionUpRes)
                    v.performClick()
                }
            }
            true
        }
    }

    /**
     *  Camera Permission 등록
     */

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
            ::handlePermissionResult
        )

    private fun checkPermissions() {
        if (
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun handlePermissionResult(isGranted: Boolean) {
        if (isGranted) {
            startCamera()
        } else {
            Snackbar.make(requireView(), "권한 받아 오기 실패", Snackbar.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }


    /**
     *  카메라 기능
     */

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
        cameraProviderFuture.addListener(
            setCamera(cameraProvider),
            ContextCompat.getMainExecutor(requireContext())
        )
    }

    private fun setCamera(cameraProvider: ProcessCameraProvider): Runnable = Runnable {
        val preview = Preview.Builder()
            .build()
            .also { mPreview ->
                mPreview.setSurfaceProvider(
                    binding.pvPreview.surfaceProvider
                )
            }

        imageCapture = ImageCapture.Builder()
            .build()

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageCapture
            )
        } catch (e: Exception) {
            Log.d("CameraX", "startCamera Fail", e)
        }
    }
    //실패 했을떄, imageUri 이 null 으로 반환 해야 한다.

    private fun capturePhoto() {
        val imageCapture = imageCapture ?: return
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(requireContext()),
            imageCaptureCallback()
        )
    }

    private fun imageCaptureCallback(): ImageCapture.OnImageCapturedCallback {
        return object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val buffer = image.planes[0].buffer
                val bytes = ByteArray(buffer.remaining())
                buffer.get(bytes)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                cameraViewModel.setCameraCharacteristics(image.imageInfo.rotationDegrees.toFloat())
                cameraViewModel.setBitmap(bitmap)
                image.close()
            }
        }
    }


    //사진 촬영 할때 만 나옴
    private fun toggleFlash() {
        val flashMode = cameraViewModel.toggleFlash()
        imageCapture?.flashMode = flashMode
    }
}