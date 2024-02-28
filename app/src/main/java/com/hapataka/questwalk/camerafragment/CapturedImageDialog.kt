package com.hapataka.questwalk.camerafragment


import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.hapataka.questwalk.databinding.DialogCapturedImageBinding

class CapturedImageDialog : DialogFragment() {
    private lateinit var binding: DialogCapturedImageBinding
    private val cameraViewModel: CameraViewModel by activityViewModels()
    private var bitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogCapturedImageBinding.inflate(inflater, container, false)
        initCapturedImage()
        return binding.root
    }

    private fun initCapturedImage() {
        bitmap = cameraViewModel.bitmap.value
        binding.imageView.setImageBitmap(bitmap)
    }

}