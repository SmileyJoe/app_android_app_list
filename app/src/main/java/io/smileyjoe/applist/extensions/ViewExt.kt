package io.smileyjoe.applist.extensions

import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import io.smileyjoe.applist.extensions.ViewExt.addLayoutListener
import io.smileyjoe.applist.extensions.ViewExt.below
import io.smileyjoe.applist.extensions.ViewExt.updateSize

/**
 * View extensions
 * </p>
 * - [updateSize]
 * - [addLayoutListener]
 * - [below]
 */
object ViewExt {

    /**
     * Update the size of a view
     * </p>
     * Convenience function for [updateSize]
     */
    fun View.updateSize(height: Float? = null, width: Float? = null) =
        updateSize(height = height?.toInt(), width = width?.toInt())

    /**
     * Update the size of the view, width or height
     *
     * @param height the new height
     * @param width the new width
     */
    fun View.updateSize(height: Int? = null, width: Int? = null) {
        var params = layoutParams

        if (height != null) {
            params.height = height
        }

        if (width != null) {
            params.width = width
        }

        layoutParams = params
    }

    /**
     * Add a [ViewTreeObserver.OnGlobalLayoutListener] to the view, this
     * will be removed when the [callback] param is true
     *
     * @param callback runs inside the layout listener, if true is returned the
     *                  listener is removed, else the listener will keep running
     */
    fun ViewGroup.addLayoutListener(callback: () -> Boolean) =
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (callback()) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        })

    /**
     * Position this view below another view
     *
     * @param viewAbove the view to be positioned below
     * @param marginTop any margin between the two views
     */
    fun View.below(viewAbove: View, marginTop: Int = 0) {
        y = viewAbove.y + viewAbove.measuredHeight + marginTop
    }

}