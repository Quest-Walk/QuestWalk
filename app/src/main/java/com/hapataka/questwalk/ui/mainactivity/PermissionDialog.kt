package com.hapataka.questwalk.ui.mainactivity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.DialogPermissionBinding

class PermissionDialog(val msg: String, val callback: () -> Unit) : DialogFragment() {
    private val binding by lazy { DialogPermissionBinding.inflate(layoutInflater) }

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
        initViews()
        initCloseButton()
    }

    private fun setDialogSize() {
        val displayMetrics = DisplayMetrics().also {
            (requireActivity().windowManager.defaultDisplay).getMetrics(it)
        }
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        val dialogWidth = (width * 0.75).toInt()
        val dialogHeight = (height * 0.25).toInt()

        dialog?.window?.setLayout(dialogWidth, dialogHeight)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.background_achieve_dialog)
    }

    private fun initViews() {
        binding.tvTitle.text = msg
    }

    private fun initCloseButton() {
        binding.btnCancel.setOnClickListener {
            callback()
            dismiss()
        }
        binding.btnConfirm.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", requireActivity().packageName, null)
            }
            startActivity(intent)
            dismiss()
        }
    }
}
