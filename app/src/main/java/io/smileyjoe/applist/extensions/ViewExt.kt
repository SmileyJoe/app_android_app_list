package io.smileyjoe.applist.extensions

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import io.smileyjoe.applist.extensions.ViewExt.addLayoutListener
import io.smileyjoe.applist.extensions.ViewExt.below
import io.smileyjoe.applist.extensions.ViewExt.updateSize
import io.smileyjoe.applist.objects.Margin
import io.smileyjoe.library.utils.Color

/**
 * View extensions
 * </p>
 * - [updateSize]
 * - [addLayoutListener]
 * - [below]
 */
object ViewExt {

    val View.ALPHA_VISIBLE: Float
        get() = 1f

    val View.ALPHA_INVISIBLE: Float
        get() = 0f

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

    /**
     * Measure the view before it is drawn
     */
    fun View.measure() {
        measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
    }

    /**
     * Get the current margins set the view
     *
     * @return [Margin] instance
     */
    fun View.margins() =
        Margin(this)

    val View.layoutInflater
        get() = LayoutInflater.from(context)

    val View.hitRect : Rect
        get() = with(Rect()){
            getHitRect(this)
            return@with this
        }

}