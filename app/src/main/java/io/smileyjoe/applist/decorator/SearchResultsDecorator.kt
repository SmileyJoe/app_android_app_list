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
import io.smileyjoe.applist.extensions.RecyclerViewExt.drawLayout
import io.smileyjoe.applist.extensions.RecyclerViewExt.getLayoutOffset
import io.smileyjoe.applist.extensions.RecyclerViewExt.isLastItem
import io.smileyjoe.applist.extensions.ViewExt.measure

class SearchResultsDecorator : HeadingDecorator() {

    companion object {
        private const val TAG = R.id.tag_view_type

        fun View.addType(type: Int) {
            setTag(TAG, type)
        }
    }

    private var binding: DecoratorDividerBinding? = null
    private var marginVertical: Int? = null

    private fun getBinding(recyclerView: RecyclerView): DecoratorDividerBinding =
        binding ?: run {
            binding = DecoratorDividerBinding.inflate(
                LayoutInflater.from(recyclerView.context),
                recyclerView,
                false
            ).apply {
                root.measure()
            }
            return binding!!
        }

    private fun getMarginVertical(context: Context): Int =
        marginVertical ?: run {
            marginVertical = context.resources.getDimensionPixelOffset(R.dimen.padding_medium)
            marginVertical!!
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
                    getLayoutOffset(outRect, getBinding(recyclerView).root)
                    outRect.top
                }

                else -> getMarginVertical(row.context)
            }

            outRect.setExt(
                top = top,
                bottom = if (recyclerView.isLastItem(row)) getMarginVertical(row.context) else 0
            )
        }
    }

    override fun onDraw(canvas: Canvas, recyclerView: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, recyclerView, state)

        recyclerView.children.forEach { row ->
            if (!isHeader(row)) {
                when (row.getType()) {
                    SearchResultsAdapter.VIEW_OTHER -> {
                        drawLayout(canvas, recyclerView, row, getBinding(recyclerView).root)
                    }
                }
            }
        }
    }
}