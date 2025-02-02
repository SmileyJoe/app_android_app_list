package io.smileyjoe.applist.extensions

import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.google.android.material.card.MaterialCardView
import io.smileyjoe.applist.extensions.MotionLayoutExt.refresh

/**
 * Helper functions to try and make working with a [ConstraintSet] more like working with
 * views themselves, mainly being setting the data on a view, instead of setting the data
 * on the [ConstraintSet] and passing it a view.
 *
 * Example:
 * ```
 * // Showing a view
 * view.isVisible = true
 *
 * // Updating the visibility of a motion constraint would be
 * constraintSet.setVisibility(view.id, View.VISIBLE)
 *
 * // The extension in here changes that to
 * view.isVisible(constraintSet, true)
 * ```
 *
 * Updating anything on here should be followed by calling [refresh] on the [MotionLayout] that
 * the [ConstraintSet] comes from
 */
object ConstraintSetExt {

    /**
     * Set the visibility of a view
     *
     * @param constraintSet
     * @param visible
     */
    fun View.isVisible(constraintSet: ConstraintSet, visible: Boolean) {
        val visibility = if (visible)
            View.VISIBLE
        else
            View.GONE

        constraintSet.setVisibility(id, visibility)
    }

    /**
     * The the card background color of a [MaterialCardView]
     *
     * @param constraintSet
     * @param color
     */
    fun MaterialCardView.setCardBackgroundColor(
        constraintSet: ConstraintSet,
        @ColorInt color: Int
    ) =
        constraintSet.setColorValue(
            id, "CardBackgroundColor", color
        )

    /**
     * Set the textColor of a [TextView]
     *
     * @param constraintSet
     * @param color
     */
    fun TextView.setTextColor(constraintSet: ConstraintSet, @ColorInt color: Int) =
        constraintSet.setColorValue(
            id,
            "TextColor",
            color
        )

}