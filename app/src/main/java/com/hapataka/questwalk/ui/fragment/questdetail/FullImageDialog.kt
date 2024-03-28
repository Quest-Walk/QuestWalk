package com.hapataka.questwalk.ui.fragment.questdetail

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import coil.load
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.DialogFullImageBinding

class FullImageDialog: DialogFragment() {
    private val binding by lazy { DialogFullImageBinding.inflate(layoutInflater) }
    private var imageUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageUri = it.getString("imageUri")
        }

        val transitionAnimation = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        )

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

        binding.ivQuest.load(imageUri) {
            placeholder(R.drawable.image_empty)
        }
        getDeviceSize()
    }

    private fun getDeviceSize() {
        val displayMetrics = DisplayMetrics()

        requireActivity().windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val dialogWidth = (displayMetrics.widthPixels * 0.85f).toInt()

        dialog?.window?.apply {
            setLayout(dialogWidth, dialogWidth)
            isCancelable = true
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }
}