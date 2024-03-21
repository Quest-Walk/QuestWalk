package com.hapataka.questwalk.util

import android.animation.ObjectAnimator
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.hapataka.questwalk.databinding.FragmentLoadingDialogBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoadingDialogFragment : DialogFragment() {
    private val binding by lazy { FragmentLoadingDialogBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadAnimation()
    }

    private fun loadAnimation() {
        val animationList = listOf(binding.ivLoading1, binding.ivLoading2, binding.ivLoading3)
        lifecycleScope.launch {
            var currentIdx = 0
            while (true) {
                animateImageView(animationList[currentIdx])
                currentIdx = (currentIdx + 1) % animationList.size
                delay(400)
            }
        }
    }

    private fun animateImageView(loadingView: ImageView) {
        ObjectAnimator.ofFloat(loadingView, "scaleX", 1.0f, 1.5f, 1.0f).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = 300
        }.start()
        ObjectAnimator.ofFloat(loadingView, "scaleY", 1.0f, 1.5f, 1.0f).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration - 300
        }.start()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.window?.apply {
            isCancelable = false
            setBackgroundDrawableResource(android.R.color.transparent)
        }
        return dialog
    }
}