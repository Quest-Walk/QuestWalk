package com.hapataka.questwalk.camerafragment

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentCameraBinding
import com.hapataka.questwalk.databinding.FragmentCaptureBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CaptureFragment : Fragment() {

    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }

    private val cameraViewModel: CameraViewModel by activityViewModels()
    private lateinit var binding: FragmentCaptureBinding

    private var bitmap: Bitmap? = null
    private var keyword: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCaptureBinding.inflate(inflater, container, false)

        cameraViewModel.isSucceed.observe(viewLifecycleOwner) { isSucceed ->
            if (isSucceed == null) return@observe
            binding.tvResult.text = cameraViewModel.isSucceed.value.toString()
            if (isSucceed) {
                // TODO: HomeFragment 에 결과값 전달 하기
            } else {
                // TODO: 캡쳐 이미지에 실패한 부분 그려주기값
            }
        }
        initCapturedImage()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            btnAttach.setOnClickListener {
                keyword = etKeyword.text.toString()
                CoroutineScope(Dispatchers.Main).launch {
                    cameraViewModel.postCapturedImage(keyword)
                }
            }
            ibBackBtn.setOnClickListener {
                navController.popBackStack()
            }
        }

    }

    private fun initCapturedImage() {
        bitmap = cameraViewModel.bitmap.value
        binding.ivCapturedImage.setImageBitmap(bitmap)
    }

    override fun onStop() {
        super.onStop()
        cameraViewModel.initBitmap()
    }


}