package io.smileyjoe.applist.decoration

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import io.smileyjoe.applist.R
import io.smileyjoe.applist.databinding.DecorationHeadingBinding
import io.smileyjoe.applist.extensions.IntExt.max
import io.smileyjoe.applist.extensions.RecyclerViewExt.drawLayout
import io.smileyjoe.applist.extensions.RecyclerViewExt.drawLayoutOver
import io.smileyjoe.applist.extensions.RecyclerViewExt.getLayoutOffset

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

    private fun getHelper(recyclerView: RecyclerView): HeadingHelper =
        helper ?: HeadingHelper(recyclerView).also {
            helper = it
        }

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
        } else {
            headers.forEachIndexed { i, (row, header) ->
                val binding = helper.getBinding(header)
                if (shouldDraw(row, header, binding, helper)) {
                    with(binding) {
                        resetHeading()
                        drawLayout(canvas, recyclerView, row, root)
                    }
                }
                if (i == 0) {
                    topHeadingRow = row
                    helper.topHeadingRow(header, row, binding)
                }
            }
        }
    }

    override fun onDrawOver(canvas: Canvas, recyclerView: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, recyclerView, state)
        helper?.currentBinding?.let {
            val emptySpace =
                recyclerView.measuredWidth - it.textHeading.measuredWidth - it.root.paddingStart
            drawOver(canvas, it, getX(it, (emptySpace / 2)), getY(it))
        }
    }

    private fun getY(currentBinding: DecorationHeadingBinding): Int {
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

    private fun getX(currentBinding: DecorationHeadingBinding, maxX: Int): Int {
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

    private fun drawOver(canvas: Canvas, binding: DecorationHeadingBinding, x: Int, y: Int) =
        with(binding) {
            val currentHeight = root.measuredHeight
            textHeading.background.alpha = 255 - alpha
            textHeading.layout(
                x,
                root.paddingTop,
                textHeading.measuredWidth + x,
                currentHeight - root.paddingBottom
            )
            drawLayoutOver(canvas, root, y)
        }

    private fun RecyclerView.getHeaderRows(): List<Pair<View, String>> =
        children.filter {
            it.hasHeader()
        }.map {
            Pair(it, it.getHeader()!!)
        }.toList()

}