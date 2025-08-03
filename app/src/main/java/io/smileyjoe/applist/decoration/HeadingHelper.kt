package io.smileyjoe.applist.decoration

import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.smileyjoe.applist.databinding.DecorationHeadingBinding
import io.smileyjoe.applist.extensions.IntExt.min
import io.smileyjoe.library.utils.ViewExt.measure

/**
 * Reset the heading row
 *
 * Because quick scrolling can throw jump position callbacks, it's not guaranteed that everything
 * will be back to where it needs to be when the heading is put back into the recycler.
 *
 * This just makes sure that regardless of the position callbacks, it will be reset correctly.
 */
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

/**
 * Helper class for keeping track of headers when using [HeadingDecoration]
 *
 * We need to scroll headers on and off the screen, but not all headers are known all the
 * time as the [RecyclerView] only loads in what it needs, so this keeps track of them, caches
 * them, and makes sure next and previous ones are available
 *
 * @param recyclerView that the [HeadingDecoration] is added to
 * @see [HeadingDecoration]
 */
class HeadingHelper(
    private val recyclerView: RecyclerView
) {

    // all the headings, first = the heading text, second = the binding for that heading //
    private val headings = mutableListOf<Pair<String, DecorationHeadingBinding>>()

    // the position in headings of the current floating heading //
    var currentPosition: Int = -1

    // binding to be used for the floating heading //
    val currentBinding: DecorationHeadingBinding?
        get() = getBinding(currentPosition)

    // the next heading that is coming up //
    val nextBinding: DecorationHeadingBinding?
        get() = getBinding(currentPosition + 1)

    /**
     * Keep track of the first visible row that has a heading
     *
     * @param heading
     * @param row
     * @param binding used for the header
     */
    fun topHeadingRow(heading: String, row: View, binding: DecorationHeadingBinding) {
        // handle scrolling back up the list //
        if (row.top - binding.root.measuredHeight <= 0) {
            currentPosition = getPosition(heading)
        } else {
            currentPosition = (getPosition(heading) - 1).min(0)
        }
    }

    fun clearTopHeadingRow() {
        currentPosition = -1
    }

    /**
     * Get the position in [headings] based on the heading text
     *
     * @param heading
     * @return position in [headings]
     */
    fun getPosition(heading: String): Int =
        headings.indexOfFirst { (title, _) -> title == heading }

    /**
     * Get the binding at the [position] or null
     *
     * @param position
     * @return binding or null
     */
    private fun getBinding(position: Int): DecorationHeadingBinding? =
        if (position in 0 until headings.size) {
            headings[position].second
        } else {
            null
        }

    /**
     * Get the binding for a specific heading text
     *
     * @param heading
     * @return binding
     */
    fun getBinding(heading: String): DecorationHeadingBinding =
        headings.firstOrNull { (title, _) ->
            title == heading
        }?.second
            ?: generateBinding(heading).also {
                headings.add(Pair(heading, it))
            }

    /**
     * Inflate and populate the view used for the heading
     *
     * @param title
     * @return binding for the view
     * @see DecorationHeadingBinding
     */
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