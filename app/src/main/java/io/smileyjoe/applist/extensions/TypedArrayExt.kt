package io.smileyjoe.applist.extensions

import android.content.res.TypedArray
import androidx.annotation.StyleableRes
import androidx.core.content.res.ResourcesCompat
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

}