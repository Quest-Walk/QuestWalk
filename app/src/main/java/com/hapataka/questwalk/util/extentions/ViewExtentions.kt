package com.hapataka.questwalk.util.extentions

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.hapataka.questwalk.R

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun TextView.showErrMsg(msg: String, context: Context) {
    val animShake = AnimationUtils.loadAnimation(context, R.anim.shake_error)

    setText(msg)
    visibility = View.VISIBLE
    setTextColor(resources.getColor(R.color.red))
    startAnimation(animShake)
}