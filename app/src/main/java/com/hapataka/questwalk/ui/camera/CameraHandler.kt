package com.hapataka.questwalk.ui.camera

import android.content.Context
import android.util.Log
import android.view.ScaleGestureDetector
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.Runnable

class CameraHandler(
    private val context: Context,
    private val viewLifecycleOwner: LifecycleOwner,
    private val preview: PreviewView,
) {

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var cameraController: CameraController

    private var cameraControl : CameraControl? = null
    private var cameraInfo :  CameraInfo? = null
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var flashMode = ImageCapture.FLASH_MODE_OFF

    var flashModeChanged: ((Int) -> Unit)? = null

    fun initCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProvider = cameraProviderFuture.get()
        cameraProviderFuture.addListener(
            setCamera(cameraProvider),
            ContextCompat.getMainExecutor(context)
        )
        cameraController = LifecycleCameraController(context)
        preview.controller = cameraController
    }

    private fun setCamera(cameraProvider: ProcessCameraProvider): Runnable = Runnable {
        val preview = Preview.Builder()
            .build()
            .also { mPreview ->
                mPreview.setSurfaceProvider(
                    preview.surfaceProvider
                )
            }
        imageCapture = ImageCapture.Builder().build()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                viewLifecycleOwner, cameraSelector, preview, imageCapture
            )
            cameraControl = camera.cameraControl
            cameraInfo = camera.cameraInfo
            setZoomInZoomOut()
        } catch (e: Exception) {
            Log.d("CameraX", "initCamera Fail", e)
        }
    }

    private fun setZoomInZoomOut() {
        scaleGestureDetector = ScaleGestureDetector(context,object :ScaleGestureDetector.SimpleOnScaleGestureListener(){
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val currentZoom = cameraInfo?.zoomState?.value?.zoomRatio ?: 1.5f
                val delta = detector.scaleFactor
                cameraControl?.setZoomRatio(currentZoom * delta)
                return true
            }
        })
        preview.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            true
        }
    }

    fun capturePhoto(imageCaptureCallback: ImageCapture.OnImageCapturedCallback) {
        val imageCapture = imageCapture ?: return
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(context),
            imageCaptureCallback
        )
    }

    fun toggleFlash() {
        flashMode = if (flashMode ==ImageCapture.FLASH_MODE_OFF){
            ImageCapture.FLASH_MODE_ON
        } else{ ImageCapture.FLASH_MODE_OFF }
        imageCapture?.flashMode = flashMode
        flashModeChanged?.invoke(flashMode)
    }
}