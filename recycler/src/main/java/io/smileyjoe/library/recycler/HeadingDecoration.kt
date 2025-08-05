package io.smileyjoe.library.recycler

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import io.smileyjoe.library.recycler.RecyclerViewExt.drawLayout
import io.smileyjoe.library.recycler.RecyclerViewExt.drawLayoutOver
import io.smileyjoe.library.recycler.RecyclerViewExt.getLayoutOffset
import io.smileyjoe.library.recycler.databinding.DecorationHeadingBinding
import io.smileyjoe.library.utils.IntExt.max

/**
 * [RecyclerView.ItemDecoration] that shows a [DecorationHeadingBinding] view
 *
 * ## Usage:
 *
 * Add the decoration to the recyclerView
 * ```
 * recyclerView.addItemDecoration(HeadingDecoration())
 * ```
 *
 * Then add the header to any [RecyclerView.ViewHolder]
 * ```
 * binding.root.addHeader("Header")
 * ```
 */
open class HeadingDecoration : RecyclerView.ItemDecoration() {

    companion object {
        /**
         * Add the header to be shown
         *
         * @param header text to show
         */
        fun View.addHeader(header: String?) {
            setTag(R.id.tag_header, header)
        }
    }

    private var alpha = 255
    private var helper: HeadingHelper? = null

    // the first row in the current loaded rows that has a heading //
    private var topHeadingRow: View? = null

    /**
     * Get the header from the view
     *
     * @return the header text, or null if nothing is set
     */
    private fun View.getHeader(): String? =
        getTag(R.id.tag_header)?.toString()

    /**
     * Check if the view has a header set
     *
     * @return true if there is a header, false otherwise
     */
    protected fun View.hasHeader(): Boolean =
        !getHeader().isNullOrEmpty()

    /**
     * Get the instance of the [HeadingHelper] or create it
     *
     * @param recyclerView
     * @return helper instance
     */
    private fun getHelper(recyclerView: RecyclerView): HeadingHelper =
        helper ?: HeadingHelper(recyclerView).also {
            helper = it
        }

    /**
     * Check if the heading needs to be drawn into the row, because we draw the heading over
     * the recycler when it starts scrolling of screen, it's not always drawn into the row
     *
     * @param row row that might need the header
     * @param header text for the heading
     * @param binding heading binding to draw
     * @param helper instance of the helper
     * @return true if the header needs to be drawn, false otherwise
     */
    private fun shouldDraw(
        row: View,
        header: String,
        binding: DecorationHeadingBinding,
        helper: HeadingHelper
    ) =
        helper.currentPosition != helper.getPosition(header) || (helper.currentPosition == 0 && row.top >= binding.root.measuredHeight)

    /**
     * @see RecyclerView.ItemDecoration.getItemOffsets
     */
    override fun getItemOffsets(
        outRect: Rect,
        row: View,
        recyclerView: RecyclerView,
        state: RecyclerView.State
    ) {
        val helper = getHelper(recyclerView)
        row.getHeader()?.let { tag ->
            getLayoutOffset(outRect, helper.getBinding(tag).root)
        }
    }

    /**
     * @see RecyclerView.ItemDecoration.onDraw
     */
    override fun onDraw(canvas: Canvas, recyclerView: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, recyclerView, state)
        val helper = getHelper(recyclerView)
        val headers = recyclerView.getHeaderRows()

        if (headers.isEmpty()) {
            topHeadingRow = null
            // if there are no items, clear the heading //
            if (recyclerView.adapter?.itemCount == 0) {
                helper.clearTopHeadingRow()
            }
        } else {
            headers.forEachIndexed { i, (row, header) ->
                val binding = helper.getBinding(header)
                if (shouldDraw(row, header, binding, helper)) {
                    with(binding) {
                        resetHeading()
                        drawLayout(canvas, recyclerView, row, root)
                    }
                }
                // if this is the first visible heading, save it to be used in onDrawOver //
                if (i == 0) {
                    topHeadingRow = row
                    helper.topHeadingRow(header, row, binding)
                }
            }
        }
    }

    /**
     * @see RecyclerView.ItemDecoration.onDraw
     */
    override fun onDrawOver(canvas: Canvas, recyclerView: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, recyclerView, state)
        helper?.currentBinding?.let {
            // the space that is not taken up by the header //
            val emptySpace =
                recyclerView.measuredWidth - it.textHeading.measuredWidth - it.root.paddingStart
            drawOver(canvas, it, getDrawOverX(it, (emptySpace / 2)), getDrawOverY(it))
        }
    }

    /**
     * Get the Y position of the floating header.
     *
     * This is scrolled off screen as the next heading inside the [RecyclerView] gets to it.
     *
     * @param currentBinding that will be drawn as the floating heading
     * @return the y position
     */
    private fun getDrawOverY(currentBinding: DecorationHeadingBinding): Int {
        val rowTop = topHeadingRow?.top ?: 0
        val nextBinding = helper?.nextBinding

        if (nextBinding != null && rowTop >= currentBinding.root.measuredHeight) {
            alpha = 0
            return (rowTop - currentBinding.root.measuredHeight - nextBinding.root.measuredHeight)
                .max(0)
        } else {
            return 0
        }
    }

    /**
     * Get the X position of the floating header.
     *
     * As the [RecyclerView] is scrolled and the row moves offscreen, the floating header
     * moves from left align to center align.
     *
     * @param currentBinding that will be drawn as the floating heading
     * @param maxX the max x value it can be moved to
     * @return the x position
     */
    private fun getDrawOverX(currentBinding: DecorationHeadingBinding, maxX: Int): Int {
        val top = topHeadingRow?.top ?: 0
        val currentHeight = currentBinding.root.measuredHeight

        if (top in 1..currentHeight) {
            val movePercent = top.toFloat() / currentHeight.toFloat()
            alpha = (255 * movePercent).toInt()
            val newX = maxX - (maxX * movePercent).toInt()
            return (newX + currentBinding.textHeading.paddingStart)
                .max(maxX)
        } else {
            return maxX
        }
    }

    /**
     * Draw the floating header
     *
     * @param canvas
     * @param binding to draw
     * @param x position
     * @param y position
     */
    private fun drawOver(canvas: Canvas, binding: DecorationHeadingBinding, x: Int, y: Int) =
        with(binding) {
            val currentHeight = root.measuredHeight
            // because the background is transparent, we are moving the heading text inside //
            // the heading view, not the heading view itself //
            textHeading.background.alpha = 255 - alpha
            textHeading.layout(
                x,
                root.paddingTop,
                textHeading.measuredWidth + x,
                currentHeight - root.paddingBottom
            )
            drawLayoutOver(canvas, root, y)
        }

    /**
     * Get all the rows that need a heading to be added
     *
     * @return list of [Pair], with first = the row, and second being the heading
     */
    private fun RecyclerView.getHeaderRows(): List<Pair<View, String>> =
        children.filter {
            it.hasHeader()
        }.map {
            Pair(it, it.getHeader()!!)
        }.toList()

}