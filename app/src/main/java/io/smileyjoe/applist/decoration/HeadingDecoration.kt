package io.smileyjoe.applist.decoration

import android.graphics.Canvas
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.children
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import io.smileyjoe.applist.R
import io.smileyjoe.applist.databinding.DecorationHeadingBinding
import io.smileyjoe.applist.extensions.Extensions.getStringOrNull
import io.smileyjoe.applist.extensions.IntExt.max
import io.smileyjoe.applist.extensions.IntExt.min
import io.smileyjoe.applist.extensions.RecyclerViewExt.drawLayout
import io.smileyjoe.applist.extensions.RecyclerViewExt.getLayoutOffset
import io.smileyjoe.applist.extensions.ViewExt.margins
import io.smileyjoe.applist.extensions.ViewExt.measure

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
        fun View.addHeader(@StringRes header: Int?) {
            setTag(R.id.tag_header, header?.let { context.getStringOrNull(it) })
        }
    }

    // cache any bindings based on the header text
    private val bindings: MutableMap<String, DecorationHeadingBinding> = mutableMapOf()

    private var headings: MutableList<String> = mutableListOf()
    private var headerPosition: Int = 0
    private var headingTop: Int = -1

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
     * Get the cached binding, or inflate it and cache it
     *
     * @param heading to show
     * @param recyclerView this will be attached to
     * @return binding to add
     */
    private fun getBinding(heading: String, recyclerView: RecyclerView): DecorationHeadingBinding =
        // get the cached binding if it exists, else, create and cache it
        bindings[heading] ?: generateBinding(recyclerView, heading).also {
            bindings[heading] = it
        }

    /**
     * Generate and populate the view
     *
     * @param parent that this will be added to
     * @param title to show
     * @return binding
     */
    private fun generateBinding(parent: ViewGroup, title: String) =
        DecorationHeadingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).apply {
            textHeading.text = title
            textHeading.background.alpha = 0
            root.background.alpha = 255
            // measure the view now, so that space can be made in getItemOffsets()
            root.measure()
        }

    /**
     * @see RecyclerView.ItemDecoration.getItemOffsets
     */
    override fun getItemOffsets(
        outRect: Rect,
        row: View,
        recyclerView: RecyclerView,
        state: RecyclerView.State
    ) {
        row.getHeader()?.let { tag ->
            getLayoutOffset(outRect, getBinding(tag, recyclerView).root)
        }
    }

    /**
     * @see RecyclerView.ItemDecoration.onDraw
     */
    override fun onDraw(canvas: Canvas, recyclerView: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, recyclerView, state)
        recyclerView.getHeaderRows().forEach { (row, header) ->
            if (!headings.contains(header)) headings.add(header)
            val binding = getBinding(header, recyclerView)
            drawLayout(canvas, recyclerView, row, binding.root)
            headerPosition = row.top
            if (row.top - binding.root.measuredHeight <= 0) {
                headingTop = headings.indexOf(header)
            } else {
                headingTop = (headings.indexOf(header) - 1).min(0)
            }
        }
    }

    var alpha = 255
    override fun onDrawOver(canvas: Canvas, recyclerView: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, recyclerView, state)
        if (headings.isNotEmpty() && headingTop in 0..headings.size) {
            val heading = headings[headingTop]
            val binding = getBinding(heading, recyclerView)
            val emptySpace = recyclerView.measuredWidth - binding.textHeading.measuredWidth - binding.root.paddingStart - binding.root.paddingEnd

            drawOver(
                canvas = canvas,
                binding = binding,
                x = getDrawOverX(binding, emptySpace/2),
                y = getDrawOverY(binding, recyclerView)
            )
        }
    }

    private fun getDrawOverY(binding: DecorationHeadingBinding, recyclerView: RecyclerView): Int {
        val nextPos = (headingTop + 1).max(headings.size-1, -1)
        val currentHeight = binding.root.measuredHeight

        if(nextPos > -1 && headerPosition >= currentHeight){
            val headingNext = headings[nextPos]
            alpha = 0
            with(getBinding(headingNext, recyclerView)){
                return (headerPosition - currentHeight - root.measuredHeight).max(0)
            }
        } else {
            return 0
        }
    }

    private fun getDrawOverX(binding: DecorationHeadingBinding, maxX: Int): Int {
        val currentHeight = binding.root.measuredHeight
        if (headerPosition in 1..currentHeight) {
            val movePercent = headerPosition.toFloat() / currentHeight.toFloat()
            alpha = (255 * movePercent).toInt()
            return maxX - (maxX * movePercent).toInt()
        } else {
            return maxX
        }
    }

    private fun drawOver(canvas: Canvas, binding: DecorationHeadingBinding, x: Int, y: Int) = with(binding){
        val margins = binding.root.margins()
        val currentHeight = binding.root.measuredHeight
        textHeading.background.alpha = 255 - alpha
        container.background.alpha = alpha
        textHeading.layout(
            x,
            binding.root.paddingTop,
            binding.textHeading.measuredWidth + x,
            currentHeight - binding.root.paddingBottom
        )
        canvas.apply {
            save()
            translate(margins.start.toFloat(), y.toFloat())
            binding.root.draw(this)
            restore()
        }
    }

    private fun RecyclerView.getHeaderRows(): List<Pair<View, String>> =
        children.filter {
            !it.getHeader().isNullOrBlank()
        }.map {
            Pair(it, it.getHeader()!!)
        }.toList()

}