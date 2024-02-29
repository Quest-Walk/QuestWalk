package com.hapataka.questwalk.ui.quest.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class QuestAdapterDecoration : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val topMargin = pxToDp(16, view)
        val horizontalMargin = pxToDp(6, view)

        outRect.set(horizontalMargin, topMargin, horizontalMargin, 0)

    }

    private fun pxToDp(dp: Int, view: View): Int {
        val density = view.resources.displayMetrics.density
        return (dp * density + 0.5f).toInt()
    }
}