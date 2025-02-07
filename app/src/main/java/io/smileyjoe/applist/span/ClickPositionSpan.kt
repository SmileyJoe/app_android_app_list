package io.smileyjoe.applist.span

import android.text.style.ClickableSpan
import android.view.View

/**
 * Clickable span that provides it's position
 *
 * @param position
 * @param click callback when the span is clicked
 */
class ClickPositionSpan(
    private val position: Int,
    private val click: (position: Int) -> Unit
) : ClickableSpan() {

    override fun onClick(widget: View) {
        click(position)
    }

}