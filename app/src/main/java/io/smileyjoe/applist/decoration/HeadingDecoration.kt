package io.smileyjoe.applist.decoration

import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import io.smileyjoe.applist.R
import io.smileyjoe.applist.databinding.DecorationHeadingBinding
import io.smileyjoe.applist.extensions.Extensions.getStringOrNull
import io.smileyjoe.applist.extensions.RecyclerViewExt.drawLayout
import io.smileyjoe.applist.extensions.RecyclerViewExt.getLayoutOffset
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
        recyclerView.children.forEach { row ->
            row.getHeader()?.let { tag ->
                drawLayout(canvas, recyclerView, row, getBinding(tag, recyclerView).root)
            }
        }
    }

}