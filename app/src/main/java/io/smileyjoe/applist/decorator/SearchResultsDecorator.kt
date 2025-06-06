package io.smileyjoe.applist.decorator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import io.smileyjoe.applist.R
import io.smileyjoe.applist.adapter.SearchResultsAdapter
import io.smileyjoe.applist.databinding.DecoratorDividerBinding
import io.smileyjoe.applist.extensions.Extensions.setExt
import io.smileyjoe.applist.extensions.ViewExt.margins
import io.smileyjoe.applist.extensions.ViewExt.measure

class SearchResultsDecorator : HeadingDecorator() {

    companion object {
        private const val TAG = R.id.tag_view_type

        fun View.addType(type: Int) {
            setTag(TAG, type)
        }
    }

    private var binding: DecoratorDividerBinding? = null
    private var marginTop: Int? = null

    private fun getBinding(recyclerView: RecyclerView): DecoratorDividerBinding {
        if (binding == null) {
            binding = DecoratorDividerBinding.inflate(
                LayoutInflater.from(recyclerView.context),
                recyclerView,
                false
            ).apply {
                root.measure()
            }
        }
        return binding!!
    }

    private fun getMarginTop(context: Context): Int {
        if (marginTop == null) {
            marginTop = context.resources.getDimensionPixelOffset(R.dimen.padding_medium)
        }

        return marginTop!!
    }

    private fun View.getType(): Int? =
        getTag(TAG)?.let {
            it as Int
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
            val top = when (row.getType()) {
                SearchResultsAdapter.VIEW_OTHER -> {
                    with(getBinding(recyclerView).root) {
                        measuredHeight + margins().vertical
                    }
                }

                else -> getMarginTop(row.context)
            }
            val bottom =
                if (recyclerView.getChildAdapterPosition(row) == recyclerView.adapter?.itemCount?.minus(
                        1
                    )
                ) {
                    getMarginTop(row.context)
                } else {
                    0
                }
            outRect.setExt(
                top = top,
                bottom = bottom
            )
        }
    }

    override fun onDraw(canvas: Canvas, recyclerView: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, recyclerView, state)

        recyclerView.children.forEach { row ->
            if (!isHeader(row)) {
                when (row.getType()) {
                    SearchResultsAdapter.VIEW_OTHER -> {
                        val header = getBinding(recyclerView).root
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
    }
}