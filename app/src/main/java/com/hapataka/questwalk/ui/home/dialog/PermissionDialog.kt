package com.hapataka.questwalk.ui.home.dialog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.DialogPermissionBinding

class PermissionDialog(val msg: String, val negativeCallback: () -> Unit, val positiveCallback: () -> Unit) : DialogFragment() {
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
        val dialogWidth = (width * 0.85).toInt()
        val dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT

        dialog?.window?.setLayout(dialogWidth, dialogHeight)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.background_achieve_dialog)
    }

    private fun initViews() {
        binding.tvTitle.text = msg
    }

    private fun initCloseButton() {
        binding.btnCancel.setOnClickListener {
            negativeCallback()
            dismiss()
        }
        binding.btnConfirm.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", requireActivity().packageName, null)
                highlightSettingsTo(EXTRA_SYSTEM_ALERT_WINDOW)
            }
            startActivity(intent)
            positiveCallback()
            dismiss()
        }
    }
    val EXTRA_FRAGMENT_ARG_KEY = ":settings:fragment_args_key"
    val EXTRA_SHOW_FRAGMENT_ARGUMENTS = ":settings:show_fragment_args"
    val EXTRA_SYSTEM_ALERT_WINDOW = "system_alert_window"

    private fun Intent.highlightSettingsTo(string: String): Intent {
        putExtra(EXTRA_FRAGMENT_ARG_KEY, string)
        val bundle = bundleOf(EXTRA_FRAGMENT_ARG_KEY to string)
        putExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS, bundle)
        return this
    }
}
