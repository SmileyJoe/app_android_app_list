package io.smileyjoe.applist.util

import android.content.Context
import android.util.Log
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt

object ThemeUtil {

    @ColorInt
    fun getColor(context: Context, @AttrRes attr: Int): Int {
        var typedValue = TypedValue()
        var theme = context.theme
        theme.resolveAttribute(attr, typedValue, true);
        val color = typedValue.data
        Log.i("AnimThings", "Color $color")
        return color
    }

}