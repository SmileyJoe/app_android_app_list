package io.smileyjoe.library.utils

import android.text.Editable

object Extensions {
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
}