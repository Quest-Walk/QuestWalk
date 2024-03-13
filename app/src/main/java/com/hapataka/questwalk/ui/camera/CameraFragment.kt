package com.hapataka.questwalk.ui.camera

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
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


@AndroidEntryPoint
class CameraFragment : BaseFragment<FragmentCameraBinding>(FragmentCameraBinding::inflate) {
    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }
    private val cameraViewModel: CameraViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()

    private var isComingFromSettings = false

    // 카메라 관련 변수
    private lateinit var cameraHandler: CameraHandler

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraHandler = CameraHandler(requireContext(), this, binding.pvPreview)
        checkPermission()
        setObserver()
        bindingWidgets()
    }

    override fun onResume() {
        super.onResume()
        if (isComingFromSettings) {
            checkPermission()
            isComingFromSettings = false
        }
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
                    cameraHandler.capturePhoto(imageCaptureCallback())
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
                cameraHandler.toggleFlash(cameraViewModel.toggleFlash())
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

    private fun checkPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                cameraHandler.initCamera()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                Snackbar.make(requireView(), "카메라를 사용하기 위해서는 권한이 필요합니다.", Snackbar.LENGTH_SHORT)
                    .setAction("확인") {
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                    .show()
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun handlePermissionResult(isGranted: Boolean) {
        if (isGranted) {
            cameraHandler.initCamera()
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                checkPermission()
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
//                navController.popBackStack()
            }
        }
    }


    private fun imageCaptureCallback(): ImageCapture.OnImageCapturedCallback {
        return object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val buffer = image.planes[0].buffer
                val bytes = ByteArray(buffer.remaining())
                buffer.get(bytes)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                val copyBitmap = drawBoxOnBitmap(bitmap,context!!)
                val croppedBitmap = cropBitmap(bitmap,context!!)
                cameraViewModel.setCameraCharacteristics(image.imageInfo.rotationDegrees.toFloat())
                cameraViewModel.setBitmap(copyBitmap)
                cameraViewModel.setCroppedBitmap(croppedBitmap)
                image.close()
                //
            }
        }
    }

    private fun dpToPx(dp: Int, context: Context): Float =
        dp * context.resources.displayMetrics.density

    fun drawBoxOnBitmap(capturedBitmap: Bitmap, context: Context): Bitmap {
        val newBitmap = capturedBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(newBitmap)
        val paint = Paint().apply {
            color = Color.RED // 박스의 색상 설정
            style = Paint.Style.STROKE // 박스를 채우지 않고 테두리만 그림
            strokeWidth = 5f // 박스 테두리의 두께
        }

        // 비트맵의 가로, 세로 크기
        val bitmapWidth = newBitmap.width
        val bitmapHeight = newBitmap.height

        // ±100dp를 px 단위로 변환
        val dp = 50
        val offsetPx = dpToPx(dp, context)

        // 박스의 좌상단, 우하단 좌표 계산
        val left = (bitmapWidth / 2) - offsetPx
        val right = (bitmapWidth / 2) + offsetPx
        val top = 0f // 세로는 비트맵을 꽉 채우므로 0에서 시작
        val bottom = bitmapHeight.toFloat() // 세로는 비트맵 끝까지

        // 비트맵 위에 박스 그리기
        canvas.drawRect(left, top, right, bottom, paint)

        return newBitmap
    }
    fun cropBitmap(capturedBitmap: Bitmap, context: Context): Bitmap {
        // ±100dp를 px 단위로 변환
        val offsetPx = dpToPx(50, context).toInt()

        // 크롭할 영역의 좌상단 좌표 계산
        val x = (capturedBitmap.width / 2) - offsetPx
        val y = 0 // 세로는 비트맵을 꽉 채우므로 0에서 시작

        // 크롭할 영역의 가로, 세로 크기
        val width = 2 * offsetPx // 가로 ±100dp
        val height = capturedBitmap.height // 세로는 비트맵 끝까지

        // 지정한 영역에 해당하는 새로운 비트맵 생성
        return Bitmap.createBitmap(capturedBitmap, x, y, width, height)
    }

}