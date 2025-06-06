package io.smileyjoe.applist.decorator

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.smileyjoe.applist.R
import io.smileyjoe.applist.extensions.Extensions.setExt
import io.smileyjoe.applist.extensions.RecyclerViewExt.isLastItem

class SearchResultsDecorator : HeadingDecorator() {

    private var marginVertical: Int? = null

    private fun getMarginVertical(context: Context): Int =
        marginVertical ?: run {
            marginVertical = context.resources.getDimensionPixelOffset(R.dimen.padding_medium)
            marginVertical!!
        }

    override fun getItemOffsets(
        outRect: Rect,
        row: View,
        recyclerView: RecyclerView,
        state: RecyclerView.State
    ) {
        if (isHeader(row)) {
            super.getItemOffsets(outRect, row, recyclerView, state)
        } else {
            val margin = getMarginVertical(row.context)
            outRect.setExt(
                top = margin,
                bottom = if (recyclerView.isLastItem(row)) margin else 0
            )
        }
    }
}