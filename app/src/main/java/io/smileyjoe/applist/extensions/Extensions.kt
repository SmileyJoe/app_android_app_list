package io.smileyjoe.applist.extensions

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


}