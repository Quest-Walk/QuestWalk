//package com.hapataka.questwalk.ui.questdetail
//
//import android.app.Dialog
//import android.graphics.drawable.ColorDrawable
//import android.os.Bundle
//import android.transition.TransitionInflater
//import android.util.DisplayMetrics
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.compose.ui.graphics.Color
//import androidx.fragment.app.DialogFragment
//import androidx.fragment.app.Fragment
//import coil.load
//import com.hapataka.questwalk.R
//import com.hapataka.questwalk.databinding.DialogFullImageBinding
//import com.hapataka.questwalk.ui.quest.QuestData
//
//class FullImageDialog: DialogFragment() {
//    private val binding by lazy { DialogFullImageBinding.inflate(layoutInflater) }
//    private var imageUri: String? = null
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val dialog = super.onCreateDialog(savedInstanceState)
//        dialog.window?.attributes?.windowAnimations = R.style.FullImageDialog
//        return dialog
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            imageUri = it.getString("imageUri")
//        }
//
//        val transitionAnimation = TransitionInflater.from(requireContext()).inflateTransition(
//            android.R.transition.move
//        )
//
//        sharedElementEnterTransition = transitionAnimation
//        sharedElementReturnTransition = transitionAnimation
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        postponeEnterTransition()
//
//        binding.ivQuest.load(imageUri) {
//            placeholder(R.drawable.image_empty)
//            listener(
//                onSuccess = {_, _ ->
//                    startPostponedEnterTransition()
//                }
//            )
//        }
//        getDeviceSize()
//    }
//
//    private fun getDeviceSize() {
//        val displayMetrics = DisplayMetrics()
//
//        requireActivity().windowManager?.defaultDisplay?.getMetrics(displayMetrics)
//        val dialogWidth = (displayMetrics.widthPixels * 0.9f).toInt()
//        val dialogHeight = (displayMetrics.widthPixels * 0.9f).toInt()
//
//        dialog?.window?.apply {
//            setLayout(dialogWidth, dialogHeight)
//            isCancelable = true
//            setBackgroundDrawableResource(android.R.color.transparent)
//        }
//    }
//}
