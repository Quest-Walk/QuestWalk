package com.hapataka.questwalk.camerafragment

import android.app.ProgressDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.CircularProgressDrawable.ProgressDrawableSize
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

    private lateinit var progressDialog :ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCaptureBinding.inflate(inflater, container, false)

        cameraViewModel.isSucceed.observe(viewLifecycleOwner) { isSucceed ->
            if (isSucceed == null) return@observe
            binding.tvResult.text = cameraViewModel.isSucceed.value.toString()
            if (isSucceed) {
                // TODO: HomeFragment 에 결과값 전달 하기함
                navController.popBackStack(R.id.frag_home,false)
            } else {
                cameraViewModel.failedImageDrawWithCanvas()
                binding.clCheckOcr.visibility = View.GONE
                binding.clResultOcr.visibility = View.VISIBLE
            }
        }
        initCapturedImage()
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("분석중....")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            btnAttach.setOnClickListener {
                keyword = etKeyword.text.toString()
                CoroutineScope(Dispatchers.Main).launch {
                    progressDialog.show()
                    cameraViewModel.postCapturedImage(keyword)
                    progressDialog.dismiss()
                }
            }
            ibBackBtn.setOnClickListener {
                navController.popBackStack()
            }
            btnResult.setOnClickListener {
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