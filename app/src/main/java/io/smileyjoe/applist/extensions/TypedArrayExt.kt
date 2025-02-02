package io.smileyjoe.applist.extensions

import android.content.res.TypedArray
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.StyleableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.getColorOrThrow
import io.smileyjoe.applist.extensions.TypedArrayExt.getResId
import java.lang.IllegalArgumentException

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

    @ColorInt
    fun TypedArray.getColor(@StyleableRes id: Int): Int? =
        try {
            getColorOrThrow(id)
        } catch (e: IllegalArgumentException){
            null
        }


}