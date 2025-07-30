package io.smileyjoe.applist.extensions

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.os.Handler
import androidx.annotation.StringRes
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

    /**
     * Helper to set the values, anything not passed in defaults to 0
     *
     * @see Rect.set
     */
    fun Rect.setExt(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) =
        set(left, top, right, bottom)

    /**
     * Check if the [term] exists, in part, or in full, in any of the items.
     *
     * @param term to look for
     * @param ignoreCase
     * @return true if any of the elements contain the [term]
     * @see String.contains
     */
    fun List<String>?.contains(term: String, ignoreCase: Boolean = false): Boolean {
        this?.forEach {
            if (it.contains(term, ignoreCase = ignoreCase)) {
                return true
            }
        }
        return false
    }

    /**
     * Get the string for the provided [id], or return null if the [id] is [Resources.ID_NULL],
     * or is not found
     *
     * @param id
     * @return the string value, or null
     */
    fun Context.getStringOrNull(@StringRes id: Int): String? =
        try {
            if (id == Resources.ID_NULL) null else getString(id)
        } catch (e: Resources.NotFoundException) {
            null
        }

    fun Context.runOnUiThread(task: () -> Unit){
        Handler(mainLooper).post {
            task()
        }
    }
}