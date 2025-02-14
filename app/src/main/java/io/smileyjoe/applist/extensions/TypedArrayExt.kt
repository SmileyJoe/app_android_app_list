package io.smileyjoe.applist.extensions

import android.content.res.TypedArray
import androidx.annotation.ColorInt
import androidx.annotation.StyleableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.getColorOrThrow
import io.smileyjoe.applist.extensions.TypedArrayExt.getResId

/**
 * [TypedArray] extensions
 * </p>
 * - [getResId]
 */
object TypedArrayExt {

    /**
     * Simple helper to save having to send a default. Sets the default to [ResourcesCompat.ID_NULL]
     */
    fun TypedArray.getResId(@StyleableRes id: Int) =
        getResourceId(id, ResourcesCompat.ID_NULL)

    /**
     * Simple helper to save having to send a default, will return null if the [id] doesn't
     * exist
     */
    @ColorInt
    fun TypedArray.getColor(@StyleableRes id: Int): Int? =
        try {
            getColorOrThrow(id)
        } catch (e: IllegalArgumentException) {
            null
        }

    /**
     * Simple helper to save having to send a default. Sets the default to 0
     */
    fun TypedArray.getDimensionPixelOffset(@StyleableRes id: Int): Int =
        getDimensionPixelOffset(id, 0)


}