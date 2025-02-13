package io.smileyjoe.library.utils

import android.graphics.Paint
import android.graphics.Typeface
import android.text.Editable
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.TextView

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

    /**
     * Adds a onGlobalLayoutListener and handles removing it when needed
     *
     * @param validate if this returns true, [listener] will be invoked and the globallayoutlistener will be removed
     * @param listener called when [validate] returns true
     */
    inline fun <T : View> T.layoutListener(
        crossinline validate: T.() -> Boolean,
        crossinline listener: T.() -> Unit
    ) {
        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (validate()) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    listener()
                }
            }
        })
    }

    /**
     * Truncate the text in the view making sure it fits in the provided [width]
     *
     * @param width that the text must be less than
     */
    fun TextView.truncate(width: Float) {
        text = text.toString().truncate(width, textSize)
    }

    /**
     * Truncate the text to make sure it is less than the [width]
     *
     * @param width that the String must be less then when drawn
     * @param textSize that the text will be drawn as
     * @return the truncated string
     */
    fun String.truncate(width: Float, textSize: Float): String {
        // newText contains the text with the ellipsis, so that the last letter can be dropped //
        var newText = this
        // display text has the ellipsis, and is the text that is measured //
        var displayText = newText
        // loop while the measured text is to big //
        while (displayText.measure(textSize) > width) {
            // drop the last letter //
            newText = newText.dropLast(1)
            // add the ellipsis and repeat //
            displayText = newText.ellipsize()
        }

        return displayText
    }

    /**
     * Add ellipsis
     *
     * @return a new ellipsized string
     */
    fun String.ellipsize(): String {
        if (last() == ' ') {
            return plus("...")
        } else {
            return plus(" ...")
        }
    }

    /**
     * Measure the text when it will be drawn at the given [size]
     *
     * @param size textSize of the string when it will be drawn
     * @return the width the text will take up when drawn
     */
    fun String.measure(size: Float) =
        Paint().apply {
            typeface = Typeface.DEFAULT
            textSize = size
        }.measureText(this)
}