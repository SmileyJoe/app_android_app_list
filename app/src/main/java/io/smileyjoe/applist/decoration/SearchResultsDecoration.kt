package io.smileyjoe.applist.decoration

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.smileyjoe.applist.R
import io.smileyjoe.library.recycler.SectionDecoration
import io.smileyjoe.library.recycler.RecyclerViewExt.isLastItem
import io.smileyjoe.library.utils.Extensions.setExt

/**
 * [RecyclerView.ItemDecoration] for the search results that adds a heading and spacing
 *
 * @param context
 */
class SearchResultsDecoration(context: Context) : SectionDecoration() {

    // spacing for the top of each row, and the bottom of the last
    private val marginVertical: Int =
        context.resources.getDimensionPixelOffset(R.dimen.padding_medium)

    /**
     * @see RecyclerView.ItemDecoration.getItemOffsets
     */
    override fun getItemOffsets(
        outRect: Rect,
        row: View,
        recyclerView: RecyclerView,
        state: RecyclerView.State
    ) {
        // if this is a header row, the parent takes care of it
        if (row.hasSection()) {
            super.getItemOffsets(outRect, row, recyclerView, state)
        } else {
            // else, set the top, and if it's the last row, set the bottom
            outRect.setExt(
                top = marginVertical,
                bottom = if (recyclerView.isLastItem(row)) marginVertical else 0
            )
        }
    }
}