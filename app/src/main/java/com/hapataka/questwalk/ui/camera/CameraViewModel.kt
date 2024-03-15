package com.hapataka.questwalk.ui.camera

import androidx.camera.core.ImageCapture
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(private val repository: CameraRepository) : ViewModel() {
    private var flashMode = ImageCapture.FLASH_MODE_OFF
    // 해당 imageFile 경로
    var file: File? = null

    fun toggleFlash(): Int {
        flashMode = if (flashMode == ImageCapture.FLASH_MODE_OFF) {
            ImageCapture.FLASH_MODE_ON
        } else {
            ImageCapture.FLASH_MODE_OFF
        }
        return flashMode
    }
}