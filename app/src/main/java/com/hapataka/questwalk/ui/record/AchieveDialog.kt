package com.hapataka.questwalk.ui.record

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import coil.load
import com.hapataka.questwalk.R
import com.hapataka.questwalk.databinding.DialogAchievementBinding
import com.hapataka.questwalk.ui.record.model.RecordItem

class AchieveDialog(val item: RecordItem) : DialogFragment() {
    private val binding by lazy { DialogAchievementBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCloseButton()
        setDialogSize()
        if (item is RecordItem.Achievement) {
            binding.tvDiscription.text = item.description
            binding.ivIcon.load(item.icon)
        }
    }

    private fun initCloseButton() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun setDialogSize() {
        val displayMetrics = DisplayMetrics().also {
            (requireActivity().windowManager.defaultDisplay).getMetrics(it)
        }
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        val dialogWidth = (width * 0.75).toInt()
        val dialogHeight = (height * 0.4).toInt()

        dialog?.window?.setLayout(dialogWidth, dialogHeight)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.background_achieve_dialog)
    }
}