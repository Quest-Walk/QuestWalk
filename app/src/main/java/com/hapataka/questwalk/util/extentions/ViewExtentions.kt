package com.hapataka.questwalk.util.extentions

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.hapataka.questwalk.R
import java.text.DecimalFormat
import kotlin.math.round

const val SIMPLE_TIME = 0
const val DETAIL_TIME = 1

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

fun Long.convertTime(code: Int): String {
    when (code) {
        SIMPLE_TIME -> {
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

        DETAIL_TIME -> {
            val second = this % 60
            val totalMinute = this / 60
            val minute = totalMinute % 60
            val hour = totalMinute / 60
            val result = "${hour}시간 ${minute}분 ${second}초"

            return result
        }

        else -> throw IllegalArgumentException("Unknown code type")
    }
}

fun Long.convertKcal(): String {
    val kcal = round(this * 0.06f)
    return kcal.toString() + "Kcal"
}

fun Float.convertKm(): String {
    val df = DecimalFormat("#.##")
    if (this < 1000f) {
        val meter = this.toInt()
        return "${meter}m"
    }
    val km = this / 1000.0f
    return df.format(km) + "km"
}
