package com.hapataka.questwalk.ui.camera

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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



        initCapturedImage()
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("분석중....")
        setObserver()
        return binding.root
    }

    private fun setObserver() {
        cameraViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) progressDialog.show()
            else progressDialog.dismiss()
        }
        cameraViewModel.isSucceed.observe(viewLifecycleOwner) { isSucceed ->
            if (isSucceed == null) return@observe
            binding.tvResult.text = cameraViewModel.isSucceed.value.toString()
            if (isSucceed) {
                homeViewModel.setImagePath(cameraViewModel.file.path)
                navController.popBackStack(R.id.frag_home, false)
            } else {
                cameraViewModel.failedImageDrawWithCanvasByMLKit()
                binding.clCheckOcr.visibility = View.GONE
                binding.clResultOcr.visibility = View.VISIBLE
                cameraViewModel.initBitmap()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            btnAttach.setOnClickListener {
                keyword = etKeyword.text.toString()
                etKeyword.clearFocus()
                hideKeyboard()
                cameraViewModel.postCapturedImageWithMLKit(keyword)

            }
            ibBackBtn.setOnClickListener {
                navController.popBackStack()
            }
            btnResult.setOnClickListener {
                navController.popBackStack()
            }
        }

    }

    // 키보드 내리기
    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun initCapturedImage() {
        bitmap = cameraViewModel.bitmap.value
        binding.ivCapturedImage.setImageBitmap(bitmap)
    }



}