package com.hapataka.questwalk.util

import android.view.MotionEvent
import android.view.View

abstract class OnSingleTouchListener: View.OnTouchListener {
    private var lastTouchTime = 0L
    abstract fun onSingleTouchUp(v: View, event: MotionEvent)
    abstract fun onSingleTouchDown(v: View, event: MotionEvent)
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val currentTouchTime = System.currentTimeMillis()
        val checkTime = currentTouchTime - lastTouchTime

        lastTouchTime = currentTouchTime
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (checkTime > 3000L) onSingleTouchUp(v, event)
            }
            MotionEvent.ACTION_UP -> onSingleTouchDown(v, event)
        }
        return true
    }
}