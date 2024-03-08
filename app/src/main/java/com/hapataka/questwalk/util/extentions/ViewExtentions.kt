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

fun Long.convertTime(): String {
    val second = this % 60
    val minute = this / 60
    val displaySecond = if (second < 10) "0$second" else second.toString()
    val displayMinute = when (minute) {
        0L -> "00"
        in 1..9 -> "0$minute"
        else -> minute.toString()
    }

    return "$displayMinute:$displaySecond"
}
