package io.smileyjoe.applist.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import io.smileyjoe.applist.extensions.TextViewExt.setDrawable

/**
 * [TextView] extensions
 * </p>
 * - [setDrawable]
 */
object TextViewExt {

    /**
     * Sets the specified drawable resource to the [TextView]
     *
     * @param left left drawable
     * @param top top drawable
     * @param right right drawable
     * @param bottom bottom drawable
     */
    fun TextView.setDrawable(
        @DrawableRes left: Int? = null,
        @DrawableRes top: Int? = null,
        @DrawableRes right: Int? = null,
        @DrawableRes bottom: Int? = null
    ) =
        setCompoundDrawablesWithIntrinsicBounds(
            getDrawable(context, left),
            getDrawable(context, top),
            getDrawable(context, right),
            getDrawable(context, bottom)
        )

    /**
     * Get a [Drawable] from a resource id
     *
     * @param context current context
     * @param drawableRes resource to load
     * @return the drawable, or null if [drawableRes] is null
     */
    private fun getDrawable(context: Context, @DrawableRes drawableRes: Int?): Drawable? =
        drawableRes?.let { ContextCompat.getDrawable(context, it) }

}