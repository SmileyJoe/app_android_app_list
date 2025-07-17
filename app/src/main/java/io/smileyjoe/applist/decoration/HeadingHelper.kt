package io.smileyjoe.applist.decoration

import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.smileyjoe.applist.databinding.DecorationHeadingBinding
import io.smileyjoe.applist.extensions.IntExt.min
import io.smileyjoe.applist.extensions.ViewExt.measure

fun DecorationHeadingBinding.resetHeading() {
    textHeading.apply {
        layout(
            paddingStart,
            root.paddingTop,
            measuredWidth + paddingStart,
            root.measuredHeight - root.paddingBottom
        )
        background.alpha = 0
    }
}

class HeadingHelper(
    private val recyclerView: RecyclerView
) {

    private val headings = mutableListOf<Pair<String, DecorationHeadingBinding>>()
    var currentPosition: Int = -1
    val currentBinding: DecorationHeadingBinding?
        get() = getBinding(currentPosition)
    val nextBinding: DecorationHeadingBinding?
        get() = getBinding(currentPosition + 1)

    fun topHeadingRow(heading: String, row: View, binding: DecorationHeadingBinding) {
        if (row.top - binding.root.measuredHeight <= 0) {
            currentPosition = getPosition(heading)
        } else {
            currentPosition = (getPosition(heading) - 1).min(0)
        }
    }

    fun getPosition(heading: String): Int =
        headings.indexOfFirst { (title, _) -> title == heading }

    private fun getBinding(position: Int): DecorationHeadingBinding? =
        if (position in 0 until headings.size) {
            headings[position].second
        } else {
            null
        }

    fun getBinding(heading: String): DecorationHeadingBinding =
        headings.firstOrNull { (title, _) ->
            title == heading
        }?.second
            ?: generateBinding(heading).also {
                headings.add(Pair(heading, it))
            }

    private fun generateBinding(title: String) =
        DecorationHeadingBinding.inflate(
            LayoutInflater.from(recyclerView.context),
            recyclerView,
            false
        ).apply {
            textHeading.text = title
            textHeading.background.alpha = 0
            // measure the view now, so that space can be made in getItemOffsets()
            root.measure()
        }

}