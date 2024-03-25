package com.hapataka.questwalk.ui.myinfo.dialog

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.DialogDropOutBinding

class DropOutDialog(val dropOutCallback: () -> Unit): DialogFragment() {
    private val binding by lazy { DialogDropOutBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDialogSize()
        initButton()
    }

    private fun setDialogSize() {
        val displayMetrics = DisplayMetrics().also {
            (requireActivity().windowManager.defaultDisplay).getMetrics(it)
        }
        val width = displayMetrics.widthPixels
        val dialogWidth = (width * 0.75).toInt()
        val dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT

        dialog?.window?.setLayout(dialogWidth, dialogHeight)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.background_achieve_dialog)
    }

    private fun initButton() {
        with(binding) {
            btnCancel.setOnClickListener {
                dismiss()
            }
            btnConfirm.setOnClickListener{
                dropOutCallback()
                dismiss()
            }
        }
    }
}