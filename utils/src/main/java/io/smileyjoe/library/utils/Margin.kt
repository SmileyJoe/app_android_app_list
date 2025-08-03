package io.smileyjoe.library.utils

import android.view.View
import android.view.ViewGroup
import io.smileyjoe.library.utils.ViewExt.margins

/**
 * Easier way to deal with view margins
 *
 * @param view to get the margins for
 * @see [ViewExt.margins]
 */
class Margin(val view: View) {
    private val params = view.layoutParams as ViewGroup.MarginLayoutParams
    var start = params.leftMargin
    var top = params.topMargin
    var end = params.rightMargin
    var bottom = params.bottomMargin
    var vertical = top + bottom
        private set
    var horizontal = start + end
        private set

    /**
     * Set the margins back on the [view]
     */
    fun apply() {
        params.apply {
            leftMargin = start
            topMargin = top
            rightMargin = end
            bottomMargin = bottom
        }
        view.layoutParams = params
    }
}
