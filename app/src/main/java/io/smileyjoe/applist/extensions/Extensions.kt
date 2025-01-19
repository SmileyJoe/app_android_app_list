package io.smileyjoe.applist.extensions

import androidx.fragment.app.Fragment

/**
 * General extensions that don't really have a specific place
 */
object Extensions {

    /**
     * Cycle the [Iterable] and call [block] on it, basically a convenience function for:
     * ```
     * list.forEach { item -> with(item) {
     *      // do something //
     * } }
     * ```
     *
     * @param block callback with a reference to each element
     */
    inline fun <T, R> Iterable<T>.withEach(block: T.() -> R): Unit {
        forEach {
            with(it, block)
        }
    }

    /**
     * Update the status bar color from a [Fragment]
     */
    var Fragment.statusBarColor: Int
        get() = requireActivity().window.statusBarColor
        set(value) {
            requireActivity().window.statusBarColor = value
        }
}