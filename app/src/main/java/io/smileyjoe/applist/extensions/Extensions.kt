package io.smileyjoe.applist.extensions

import android.text.Editable
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

    /**
     * Basically allows chaining of an if statement when manipulating a [MutableList]
     *
     * @param condition if true [block] will be called
     * @param block of code to run if [condition] is true
     * @return the current [MutableList]
     */
    inline fun <T> MutableList<T>.edit(
        condition: Boolean = true,
        block: MutableList<T>.() -> Unit
    ): MutableList<T> {
        if (condition) block()
        return this
    }

    /**
     * Convenience for:
     *
     * Before:
     * ```kotlin
     * nullable?.let{ with(it) {
     *      // do something
     * }}
     * ```
     *
     * After:
     * ```kotlin
     * withNotNull(nullable){
     *      // do something
     * }
     * ```
     */
    inline fun <T, R> withNotNull(receiver: T?, block: T.() -> R): R? {
        return receiver?.block()
    }

    /**
     * Pad the start and/or end of the [String] with the provided [char].
     *
     * This is different from [padStart] and [padEnd] as it will add the given number passed with
     * [start] and [end], as opposed to adding until the [String] is that length.
     *
     * @param start number of [char] to add as a prefix
     * @param end number of [char] to add as a suffix
     * @param char to add
     * @return the editing [String]
     */
    fun String.pad(start: Int, end: Int, char: Char): String {
        val new = padStart(length + start, char)
        return new.padEnd(new.length + end, char)
    }

    /**
     * Remove the all the spans of the given [type] from the [Editable]
     *
     * @param type of span to remove
     */
    fun <T> Editable.removeSpans(type: Class<T>) {
        getSpans(0, length, type).forEach { removeSpan(it) }
    }

    /**
     * Only add the items in the provided [list] if they are not already in the current list.
     *
     * @param list to add
     */
    fun <T> MutableList<T>.addDistinct(list: List<T>) {
        addAll(list)
        val distinctList = distinct()
        clear()
        addAll(distinctList)
    }
}