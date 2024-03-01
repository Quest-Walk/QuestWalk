package com.hapataka.questwalk.camerafragment


import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.hapataka.questwalk.databinding.DialogCapturedImageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnPostOcr.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch{
            cameraViewModel.postCapturedImage()
            }
        }
    }

    private fun initCapturedImage() {
        bitmap = cameraViewModel.bitmap.value
        binding.ivCapturedImage.setImageBitmap(bitmap)
    }



}