package com.hapataka.questwalk.ui.camera

import android.content.Context
import android.util.Log
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
            cameraProvider.bindToLifecycle(
                viewLifecycleOwner, cameraSelector, preview, imageCapture
            )
        } catch (e: Exception) {
            Log.d("CameraX", "initCamera Fail", e)
        }
    }

    fun capturePhoto(imageCaptureCallback: ImageCapture.OnImageCapturedCallback) {
        val imageCapture = imageCapture ?: return
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(context),
            imageCaptureCallback
        )
    }

    fun toggleFlash(flashMode: Int) {
        imageCapture?.flashMode = flashMode
    }
}