package io.smileyjoe.library.utils

import android.content.res.Resources
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import androidx.core.view.isVisible
import io.smileyjoe.library.utils.ViewExt.addLayoutListener
import io.smileyjoe.library.utils.ViewExt.below
import io.smileyjoe.library.utils.ViewExt.updateSize

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

    val View.hitRect: Rect
        get() = with(Rect()) {
            getHitRect(this)
            return@with this
        }

    fun View.padding(
        start: Int = paddingStart,
        top: Int = paddingTop,
        end: Int = paddingEnd,
        bottom: Int = paddingBottom
    ) = setPadding(start, top, end, bottom)

    fun View.show(@AnimRes animation: Int) {
        animate(
            animation = animation,
            onStart = { isVisible = true }
        )
    }

    fun View.hide(@AnimRes animation: Int) {
        animate(
            animation = animation,
            onComplete = { isVisible = false }
        )
    }

    fun View.animate(@AnimRes animation: Int, onStart: (() -> Unit)? = null, onComplete: (() -> Unit)? = null) {
        if (animation != Resources.ID_NULL) {
            AnimationUtils.loadAnimation(context, animation).also { anim ->
                anim.setListener(
                    onStart = onStart,
                    onComplete = onComplete
                )
                startAnimation(anim)
            }
        } else {
            onStart?.invoke()
            onComplete?.invoke()
        }
    }

    fun Animation.setListener(
        onStart: (() -> Unit)? = null,
        onComplete: (() -> Unit)? = null,
        onRepeat: (() -> Unit)? = null
    ) {
        setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                onStart?.invoke()
            }

            override fun onAnimationEnd(animation: Animation?) {
                onComplete?.invoke()
            }

            override fun onAnimationRepeat(animation: Animation?) {
                onRepeat?.invoke()
            }
        })
    }

}