package io.smileyjoe.applist.extensions

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import io.smileyjoe.applist.extensions.Extensions.setExt
import io.smileyjoe.applist.extensions.ViewExt.margins

object RecyclerViewExt {

    const val POSITION_TOP = -1
    const val POSITION_BOTTOM = -2
    const val POSITION_UNKNOWN = -3

    /**
     * Get the offset needed to draw the [layout] as a [RecyclerView.ItemDecoration]
     *
     * @param outRect
     * @param layout that will be drawn
     * @see RecyclerView.ItemDecoration.getItemOffsets
     */
    fun RecyclerView.ItemDecoration.getLayoutOffset(outRect: Rect, layout: View) {
        with(layout) {
            outRect.setExt(top = measuredHeight + margins().vertical)
        }
    }

    /**
     * Draw the [layout] as a [RecyclerView.ItemDecoration] on the [row]
     *
     * @param canvas
     * @param recyclerView
     * @param row
     * @param layout to draw
     * @see RecyclerView.ItemDecoration.onDraw
     */
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

    /**
     * Draw the [view] as a floating header on top of the [RecyclerView]
     *
     * @param canvas to draw onto
     * @param view to draw
     * @param y position
     */
    fun RecyclerView.ItemDecoration.drawLayoutOver(canvas: Canvas, view: View, y: Int) {
        val margins = view.margins()
        canvas.apply {
            save()
            translate(margins.start.toFloat(), y.toFloat())
            view.draw(this)
            restore()
        }
    }

    /**
     * Check if the [row] is the last item in the [RecyclerView]
     *
     * @param row
     * @return true if it's the last, false otherwise
     */
    fun RecyclerView.isLastItem(row: View): Boolean =
        getChildAdapterPosition(row) == adapter?.itemCount?.minus(1)

    val RecyclerView.itemCount : Int
        get() = adapter?.itemCount ?: -1

    val RecyclerView.positionTop : Int
        get() = (layoutManager as? LinearLayoutManager)?.findFirstCompletelyVisibleItemPosition() ?: -1

    val RecyclerView.positionBottom : Int
        get() = (layoutManager as? LinearLayoutManager)?.findLastCompletelyVisibleItemPosition() ?: -1

    val RecyclerView.position : Int
        get() = when{
            positionTop == -1 -> POSITION_UNKNOWN
            positionBottom == -1 -> POSITION_UNKNOWN
            positionTop == 0 -> POSITION_TOP
            positionBottom + 1 == itemCount -> POSITION_BOTTOM
            else -> positionTop
        }

    fun RecyclerView.smoothScrollTo(position: Int){
        val smoothScroller: RecyclerView.SmoothScroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }.apply {
            targetPosition = position
        }

        layoutManager?.startSmoothScroll(smoothScroller)
    }
}