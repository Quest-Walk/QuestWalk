package com.hapataka.questwalk.ui.camera

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hapataka.questwalk.databinding.FragmentCaptureBinding
import com.hapataka.questwalk.ui.mainactivity.MainViewModel
import com.hapataka.questwalk.ui.record.TAG
import com.hapataka.questwalk.util.BaseFragment

class CaptureFragment : BaseFragment<FragmentCaptureBinding>(FragmentCaptureBinding::inflate) {

    private val navController by lazy { (parentFragment as NavHostFragment).findNavController() }

    private val cameraViewModel: CameraViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private var bitmap: Bitmap? = null
    private var keyword: String = ""

    private lateinit var progressDialog: ProgressDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCapturedImage()
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("분석중....")
        setObserver()
        initDebug()
        with(binding) {
            ivCroppedImage.setImageBitmap(cameraViewModel.getCroppedBitmap())
            btnAttach.setOnClickListener {
                if (cameraViewModel.isDebug.value == true && etKeywordDebug.text.isNotBlank()) {
                    keyword = etKeywordDebug.text.toString()
                    etKeywordDebug.clearFocus()
                } else {
                    keyword = mainViewModel.currentKeyword.value ?: ""
                }
                hideKeyboard()

                cameraViewModel.postCapturedImageWithMLKit(keyword)

            }
            btnBack.setOnClickListener {
                navController.popBackStack()
            }
            btnResult.setOnClickListener {
                navController.popBackStack()
            }

            ibCropBtn.apply{
                setOnClickListener {
                    cameraViewModel.clickedCropImageButton()
                    Snackbar.make(requireView(),"isCropped : ${cameraViewModel.isCropped}",Snackbar.LENGTH_SHORT).show()
                }
            }
            ivCapturedImage.setOnLongClickListener {
                cameraViewModel.setDebug()
                true
            }
        }

    }

    private fun setObserver() {
        cameraViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) progressDialog.show()
            else progressDialog.dismiss()
        }
        cameraViewModel.isSucceed.observe(viewLifecycleOwner) { isSucceed ->
            if (isSucceed == null) return@observe

            if (isSucceed) {
                if (cameraViewModel.file != null) {
                    Snackbar.make(requireView(),cameraViewModel.file.toString(),Snackbar.LENGTH_SHORT).show()
//                    mainViewModel.setCaptureImage(cameraViewModel.file!!.path)
                    initCapturedImage()
                }
//                navController.popBackStack(R.id.frag_home, false)
            } else {
//                cameraViewModel.failedImageDrawWithCanvasByMLKit(keyword)
                binding.clCheckOcr.visibility = View.GONE
                binding.clResultOcr.visibility = View.VISIBLE
                cameraViewModel.initBitmap()
            }
        }
        cameraViewModel.bitmap.observe(viewLifecycleOwner){

            initCapturedImage()
        }

    }

    // 키보드 내리기
    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun initCapturedImage() {

        bitmap = cameraViewModel.getDrawBoxOnBitmap()
        binding.ivCapturedImage.setImageBitmap(bitmap)
        binding.ivCroppedImage.setImageBitmap(cameraViewModel.getCroppedBitmap())

    }

    override fun onStop() {
        super.onStop()
        cameraViewModel.initBitmap()
    }

    /**
     *  Debug
     */
    private lateinit var getContext: ActivityResultLauncher<String>
    private fun initDebug() {

        cameraViewModel.isDebug.observe(viewLifecycleOwner) { isDebug ->
            if (isDebug) {
                binding.etKeywordDebug.visibility = View.VISIBLE
                binding.btnLoadImageDebug.visibility = View.VISIBLE
                Snackbar.make(requireView(), "Debug Mode!", Snackbar.LENGTH_SHORT).show()
            } else {
                binding.etKeywordDebug.visibility = View.GONE
                binding.btnLoadImageDebug.visibility = View.GONE
            }

        }


        getContext = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val inputStream = requireActivity().contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                cameraViewModel.setBitmapByGallery(bitmap)

            }
        }
        binding.btnLoadImageDebug.setOnClickListener {
            getContext.launch("image/*")
        }
    }
}