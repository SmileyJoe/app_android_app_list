package io.smileyjoe.applist.extensions

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.smileyjoe.applist.extensions.Extensions.setExt
import io.smileyjoe.applist.extensions.ViewExt.margins

object RecyclerViewExt {

    fun RecyclerView.ItemDecoration.getLayoutOffset(outRect: Rect, layout: View) {
        with(layout) {
            outRect.setExt(top = measuredHeight + margins().vertical)
        }
    }

    fun RecyclerView.ItemDecoration.drawLayout(
        canvas: Canvas,
        recyclerView: RecyclerView,
        row: View,
        layout: View
    ) {
        with(layout) {
            val margins = margins()
            layout(
                recyclerView.left,
                0,
                recyclerView.right - margins.horizontal,
                measuredHeight
            )
            canvas.apply {
                save()
                val x = margins.start
                val y = row.top - measuredHeight - margins.bottom
                translate(x.toFloat(), y.toFloat())
                layout.draw(this)
                restore()
            }
        }
    }

    fun RecyclerView.isLastItem(row: View): Boolean =
        getChildAdapterPosition(row) == adapter?.itemCount?.minus(1)
}