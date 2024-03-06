package com.hapataka.questwalk.ui.camera

import android.app.ProgressDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.FragmentCaptureBinding
import com.hapataka.questwalk.ui.home.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CaptureFragment : Fragment() {

    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }

    private val cameraViewModel: CameraViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var binding: FragmentCaptureBinding

    private var bitmap: Bitmap? = null
    private var keyword: String = ""

    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCaptureBinding.inflate(inflater, container, false)

        cameraViewModel.isSucceed.observe(viewLifecycleOwner) { isSucceed ->
            if (isSucceed == null) return@observe
            binding.tvResult.text = cameraViewModel.isSucceed.value.toString()
            if (isSucceed) {
                homeViewModel.setImagePath(cameraViewModel.file.path)
                navController.popBackStack(R.id.frag_home, false)
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
                etKeyword.clearFocus()
                CoroutineScope(Dispatchers.Main).launch {
                    progressDialog.show()
                    try {
                        cameraViewModel.postCapturedImage(keyword)
                    } catch (e: Exception) {
                        Log.d("result",e.toString())
                        Snackbar.make(requireView(),"문자가 너무 어려워요 ㅠㅠ 다시 찍어 주세요!",Snackbar.LENGTH_SHORT).show()
                        binding.clCheckOcr.visibility = View.GONE
                        binding.clResultOcr.visibility = View.VISIBLE
                    }
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