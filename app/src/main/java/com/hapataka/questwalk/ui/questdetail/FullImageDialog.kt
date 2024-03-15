package com.hapataka.questwalk.ui.questdetail

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import coil.load
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.DialogFullImageBinding
import com.hapataka.questwalk.ui.record.TAG

class FullImageDialog : DialogFragment() {
    private val binding by lazy { DialogFullImageBinding.inflate(layoutInflater) }
    private var imageUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageUri = it.getString("imageUri")
        }

        val animation = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.explode
        )
        enterTransition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.slide_right)

//        sharedElementEnterTransition = animation
//        sharedElementReturnTransition = animation

        Log.d(TAG, "enter: ${binding.ivFullDialogQuest.transitionName}")
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
        ViewCompat.setTransitionName(binding.ivFullDialogQuest, "pair_image")
        binding.ivFullDialogQuest.load(imageUri) {
            placeholder(R.drawable.image_empty)
        }
        getDeviceSize()


    }

    private fun getDeviceSize() {
        val displayMetrics = DisplayMetrics()

        requireActivity().windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val dialogWidth = (displayMetrics.widthPixels * 0.9f).toInt()
        val dialogHeight = (displayMetrics.widthPixels * 0.9f).toInt()

        dialog?.window?.apply {
            setLayout(dialogWidth, dialogHeight)
            isCancelable = true
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }
}