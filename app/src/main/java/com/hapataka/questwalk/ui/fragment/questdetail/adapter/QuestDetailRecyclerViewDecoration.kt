package com.hapataka.questwalk.ui.fragment.questdetail.adapter

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class QuestDetailRecyclerViewDecoration (val context: Context) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val viewParam = view.layoutParams as GridLayoutManager.LayoutParams

        outRect.bottom = 12.dpToPx()

        when (viewParam.spanIndex) {
            0 -> {
                outRect.right = 8.dpToPx()
            }

            1 -> {
                outRect.right = 4.dpToPx()
                outRect.left = 4.dpToPx()
            }

            2 -> {
                outRect.left = 8.dpToPx()
            }
        }
    }

    fun <T : Number> T.dpToPx(): T {
        val value = this.toFloat()
        val result = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value,
            context.resources.displayMetrics
        )

        @Suppress("UNCHECKED_CAST")
        return when (this) {
            is Int -> result.toInt() as T
            is Float -> result as T
            is Double -> result.toDouble() as T
            is Long -> result.toLong() as T
            else -> throw IllegalArgumentException("Unconfirmed Number Type")
        }
    }
}