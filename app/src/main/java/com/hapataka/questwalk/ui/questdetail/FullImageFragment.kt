package com.hapataka.questwalk.ui.questdetail

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import coil.load
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.DialogFullImageBinding

class FullImageFragment : Fragment() {
    private val binding by lazy { DialogFullImageBinding.inflate(layoutInflater) }
    private var imageUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        requireActivity().window.setBackgroundDrawableResource(android.R.color.transparent)
        arguments?.let {
            imageUri = it.getString("imageUri")
        }

        val transitionAnimation = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        )

        enterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = transitionAnimation
        sharedElementReturnTransition = transitionAnimation
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBackPressedCallback()
        getDeviceSize()

        binding.ivQuest.load(imageUri) {
            placeholder(R.drawable.image_empty)
        }
    }

    private fun getDeviceSize() {
        val displayMetrics = DisplayMetrics()

        requireActivity().windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val dialogWidth = (displayMetrics.widthPixels * 0.8f).toInt()
        val dialogHeight = (displayMetrics.widthPixels * 0.8f).toInt()
        val layoutParams = binding.ivQuest.layoutParams

        layoutParams.width = dialogWidth
        layoutParams.height = dialogHeight
        binding.ivQuest.layoutParams = layoutParams
    }

    companion object {
        fun newInstance(param1: String) =
            FullImageFragment().apply {
                arguments = Bundle().apply {
                    putString("imageUri", param1)
                }
            }
    }


    private fun initBackPressedCallback() {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }.also {
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, it)
        }
    }
}
