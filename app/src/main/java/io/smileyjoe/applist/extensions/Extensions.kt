package io.smileyjoe.applist.extensions

import android.graphics.Rect
import androidx.fragment.app.Fragment

/**
 * General extensions that don't really have a specific place
 */
object Extensions {

    /**
     * Update the status bar color from a [Fragment]
     */
    var Fragment.statusBarColor: Int
        get() = requireActivity().window.statusBarColor
        set(value) {
            requireActivity().window.statusBarColor = value
        }

    fun Rect.setExt(left: Int = 0, top: Int = 0, right: Int = 0, bottom : Int = 0) =
        set(left, top, right, bottom)

}