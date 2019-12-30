package com.sample.themoviedb.utils.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View,
                                parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = space / 2
        outRect.right = space / 2
        outRect.bottom = space
        outRect.top = 0
    }
}