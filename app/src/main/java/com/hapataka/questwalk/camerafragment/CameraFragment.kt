package com.hapataka.questwalk.camerafragment

import android.Manifest
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.hapataka.questwalk.databinding.FragmentCameraBinding

class CameraFragment : Fragment() {


    private lateinit var viewModel: CameraViewModel
    private lateinit var binding: FragmentCameraBinding

    private val requestPermissionLauncher  =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // TODO : 카메라 실행
                Snackbar.make(requireView(), "카메라 실행", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(requireView(), "권한 받아오기 실패", Snackbar.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[CameraViewModel::class.java]
        // TODO: Use the ViewModel
        checkPermissions()
    }

    private fun checkPermissions() {
        if (
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // TODO : 카메라 실행
            Snackbar.make(requireView(), "카메라 실행", Snackbar.LENGTH_SHORT).show()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

}


private fun setCamera() {

}