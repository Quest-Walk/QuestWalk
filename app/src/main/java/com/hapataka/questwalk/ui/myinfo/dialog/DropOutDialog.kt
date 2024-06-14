package com.hapataka.questwalk.ui.myinfo.dialog

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.DialogDropOutBinding

class DropOutDialog: DialogFragment() {
    private var _binding: DialogDropOutBinding? = null
    private val binding get() = _binding!!
    var onConfirm: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =  DialogDropOutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSize()
        initButton()
    }

    private fun initButton() {
        with(binding) {
            btnCancel.setOnClickListener {
                dismiss()
            }
            btnConfirm.setOnClickListener{
                onConfirm?.invoke()
            }
        }
    }

    private fun setSize() {
        var dialogWidth: Int
        val dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val windowMetrics = requireActivity().windowManager.currentWindowMetrics

            dialogWidth = (windowMetrics.bounds.width() * 0.85).toInt()
        } else {
            @Suppress("DEPRECATION")
            val displayMetrics = DisplayMetrics().also {
                requireActivity().windowManager.defaultDisplay.getRealMetrics(it)
            }

            dialogWidth = (displayMetrics.widthPixels * 0.85).toInt()
        }
        dialog?.window?.setLayout(dialogWidth, dialogHeight)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.background_achieve_dialog)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}