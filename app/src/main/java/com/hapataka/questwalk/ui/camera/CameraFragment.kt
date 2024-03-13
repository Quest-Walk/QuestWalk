package com.hapataka.questwalk.ui.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
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
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.getMainExecutor
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentCameraBinding
import com.hapataka.questwalk.ui.mainactivity.MainViewModel
import com.hapataka.questwalk.ui.record.TAG
import com.hapataka.questwalk.util.BaseFragment
import com.hapataka.questwalk.util.ViewModelFactory
import com.hapataka.questwalk.util.extentions.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Runnable


@AndroidEntryPoint
class CameraFragment : BaseFragment<FragmentCameraBinding>(FragmentCameraBinding::inflate) {
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private val mainViewModel: MainViewModel by activityViewModels { ViewModelFactory() }
    private val cameraViewModel: CameraViewModel by activityViewModels()
    private var imageCapture: ImageCapture? = null
    private var isComingFromSettings = false
    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
            ::handlePermissionResult
        )

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkPermissions()
        setObserver()
        initViews()
    }

    private fun checkPermissions() {
        val cameraPermission = Manifest.permission.CAMERA
        val checkCameraPermission =
            checkSelfPermission(requireContext(), cameraPermission) == PERMISSION_GRANTED

        if (checkCameraPermission) {
            initCamera()
            return
        }
        requestPermissionLauncher.launch(cameraPermission)
    }

    private fun handlePermissionResult(isGranted: Boolean) {
        if (isGranted) {
            initCamera()
            return
        }

        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            Snackbar.make(requireView(), "카메라를 사용하기 위해서는 권한이 필요합니다.", Snackbar.LENGTH_SHORT)
                .setAction("확인") {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
                .show()
        } else {
            Snackbar.make(requireView(), "권한 받아 오기 실패", Snackbar.LENGTH_SHORT)
                .setAction("권한 설정") {
                    isComingFromSettings = true
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", requireActivity().packageName, null)
                    }
                    startActivity(intent)
                }
                .show()
        }


    }

    private fun setObserver() {
        with(mainViewModel) {
            imageBitmap.observe(viewLifecycleOwner) {
                Log.d(TAG, "bitmap: $it")
                with(binding.ivCapturedImage) {
                    load(it)
                    visible()
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initViews() {
        with(binding) {
            btnCapture.apply {
                setOnClickListener {
                    capturePhoto()
                }
                setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        this.load(R.drawable.btn_capture_click)
                        return@setOnTouchListener false
                    }

                    if (event.action == MotionEvent.ACTION_UP) {
                        this.load(R.drawable.btn_capture)
                        return@setOnTouchListener false
                    }
                    return@setOnTouchListener false
                }
            }
            btnFlash.setOnClickListener {
                toggleFlash()
            }
            btnBack.setOnClickListener {
                navController.popBackStack()
            }
//            tvCameraQuest.text = homeViewModel.currentKeyword.value
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
     *  카메라 기능
     */

    private fun initCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

        cameraProviderFuture.addListener(
            setCamera(cameraProvider),
            getMainExecutor(requireContext())
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
            getMainExecutor(requireContext()),
            imageCaptureCallback()
        )
    }

    private fun imageCaptureCallback(): ImageCapture.OnImageCapturedCallback {
        return object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                mainViewModel.setCaptureImage(image) {
                    navController.popBackStack()
                }
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