package com.hapataka.questwalk.ui.fragment.weather.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class WeatherAdapterDecoration: RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)

        if (position < (parent.adapter?.itemCount ?: 0) - 1) {
            outRect.right = pxToDp(24, view)
        }
    }

    private fun pxToDp(dp: Int, view: View): Int {
        val density = view.resources.displayMetrics.density
        return (dp * density + 0.5f).toInt()
    }
}