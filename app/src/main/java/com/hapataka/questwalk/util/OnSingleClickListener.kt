package com.hapataka.questwalk.util

import android.view.View

abstract class OnSingleClickListener: View.OnClickListener {
    private var lastClickTime = 0L
    abstract fun onSingleClick(v: View?)
    override fun onClick(v: View?) {
        val currentClickTime = System.currentTimeMillis()
        val checkTime = currentClickTime - lastClickTime

        lastClickTime = currentClickTime
        if (checkTime > 1500L) {
            onSingleClick(v)
        }
    }
}

