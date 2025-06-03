package io.smileyjoe.applist.decorator

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.StringRes
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import io.smileyjoe.applist.R
import io.smileyjoe.applist.databinding.DecoratorHeadingBinding
import io.smileyjoe.applist.extensions.Extensions.setExt
import io.smileyjoe.applist.extensions.ViewExt.margins
import io.smileyjoe.applist.extensions.ViewExt.measure


class HeadingDecorator : RecyclerView.ItemDecoration() {

    companion object {
        fun View.addHeader(@StringRes header: Int) {
            val text = if (header == Resources.ID_NULL) {
                null
            } else {
                context.getString(header)
            }
            setTag(R.id.tag_header, text)
        }
    }

    private val bindings: MutableMap<String, DecoratorHeadingBinding> = mutableMapOf()

    private fun getBinding(tag: String?, recyclerView: RecyclerView): DecoratorHeadingBinding? {
        return tag?.let {
            if (bindings.contains(it)) {
                bindings[it]
            } else {
                val binding = DecoratorHeadingBinding.inflate(
                    LayoutInflater.from(recyclerView.context),
                    recyclerView,
                    false
                ).apply {
                    textHeading.text = it
                    root.measure()
                }
                bindings[it] = binding
                binding
            }
        }
    }

    private fun getTag(view: View): String? =
        view.getTag(R.id.tag_header)?.toString()

    override fun getItemOffsets(
        outRect: Rect,
        row: View,
        recyclerView: RecyclerView,
        state: RecyclerView.State
    ) {
        getBinding(getTag(row), recyclerView)?.root?.let { header ->
            val margins = header.margins()
            outRect.setExt(
                top = header.measuredHeight + margins.top + margins.bottom
            )
        } ?: {
            super.getItemOffsets(outRect, row, recyclerView, state)
        }
    }

    override fun onDraw(canvas: Canvas, recyclerView: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, recyclerView, state)

        recyclerView.children.forEach { row ->
            getBinding(getTag(row), recyclerView)?.root?.let { header ->
                val margins = header.margins()
                header.layout(
                    recyclerView.left,
                    0,
                    recyclerView.right - margins.end - margins.start,
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