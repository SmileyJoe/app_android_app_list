package io.smileyjoe.applist.decorator

import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import io.smileyjoe.applist.R
import io.smileyjoe.applist.databinding.DecoratorHeadingBinding
import io.smileyjoe.applist.extensions.Extensions.getStringOrNull
import io.smileyjoe.applist.extensions.Extensions.setExt
import io.smileyjoe.applist.extensions.ViewExt.margins
import io.smileyjoe.applist.extensions.ViewExt.measure


open class HeadingDecorator : RecyclerView.ItemDecoration() {

    companion object {
        private const val TAG = R.id.tag_header
        fun View.addHeader(@StringRes header: Int) {
            setTag(TAG, context.getStringOrNull(header))
        }
    }

    private val bindings: MutableMap<String, DecoratorHeadingBinding> = mutableMapOf()

    private fun View.getHeader(): String? =
        getTag(TAG)?.toString()

    protected fun isHeader(view: View): Boolean =
        !view.getHeader().isNullOrEmpty()

    private fun getBinding(tag: String, recyclerView: RecyclerView): DecoratorHeadingBinding =
        bindings[tag] ?: run {
            val binding = generateBinding(recyclerView, tag)
            bindings[tag] = binding
            binding
        }

    private fun generateBinding(parent: ViewGroup, title: String) =
        DecoratorHeadingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).apply {
            textHeading.text = title
            root.measure()
        }

    override fun getItemOffsets(
        outRect: Rect,
        row: View,
        recyclerView: RecyclerView,
        state: RecyclerView.State
    ) {
        row.getHeader()?.let { tag ->
            with(getBinding(tag, recyclerView).root) {
                outRect.setExt(top = measuredHeight + margins().vertical)
            }
        }
    }

    override fun onDraw(canvas: Canvas, recyclerView: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, recyclerView, state)
        recyclerView.children.forEach { row ->
            row.getHeader()?.let { tag ->
                val header = getBinding(tag, recyclerView).root
                val margins = header.margins()
                header.layout(
                    recyclerView.left,
                    0,
                    recyclerView.right - margins.horizontal,
                    header.measuredHeight
                )
                canvas.apply {
                    save()
                    val x = margins.start
                    val y = row.top - header.measuredHeight - margins.bottom
                    translate(x.toFloat(), y.toFloat())
                    header.draw(this)
                    restore()
                }
            }
        }
    }

}