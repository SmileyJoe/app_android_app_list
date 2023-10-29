package io.smileyjoe.applist.util

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt

/**
 * Helper functions for dealing with theme related items
 */
object ThemeUtil {

    /**
     * Get the color as it is in the theme
     * </p>
     * eg, to get the surface color set in the applied theme:
     * </p>
     * `getColor(baseContext, R.attr.colorSurface)`
     *
     * @param context current context
     * @param attr attr of a color to get
     * @return the color
     */
    @ColorInt
    fun getColor(context: Context, @AttrRes attr: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

}