package io.smileyjoe.applist.textwatcher

import android.text.Editable
import android.text.TextWatcher
import io.smileyjoe.applist.textwatcher.CustomTextWatcher.Direction
import io.smileyjoe.applist.textwatcher.CustomTextWatcher.Direction.BACKWARD
import io.smileyjoe.applist.textwatcher.CustomTextWatcher.Direction.FORWARD

/**
 * [TextWatcher] that handles some boiler plate.
 *
 * - Works out the [Direction] of the text
 * - Handles being able to edit the text without recalling the watcher with [update]
 * - Keeps a record of the last state of the text
 */
abstract class CustomTextWatcher : TextWatcher {

    /**
     * Direction of the text, [FORWARD] if the user is adding text, [BACKWARD] if the user is
     * removing text
     */
    protected enum class Direction {
        FORWARD, BACKWARD
    }

    protected var direction: Direction = Direction.FORWARD
    protected var updating = false
        private set
    protected var editable: Editable? = null
        private set

    // the last character in the string, either the character being removed, or the character being //
    // added //
    protected var lastChar: Char? = null

    open fun afterTextChanged() {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        if (!updating) {
            if (count > after) {
                direction = BACKWARD
                lastChar = s?.lastOrNull()
            } else {
                direction = FORWARD
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    /**
     * Rather use [afterTextChanged] and [editable] instead of overriding this
     */
    override fun afterTextChanged(s: Editable?) {
        editable = s
        if (direction == FORWARD) {
            lastChar = s?.lastOrNull()
        }
        afterTextChanged()
    }

    /**
     * Changing the text withing something like [afterTextChanged] will call the watcher again
     * and create an infinite loop, wrapping any editing code in this will prevent that.
     *
     * @param block code to run to edit the contents of the [editable]
     */
    protected fun update(block: () -> Unit) {
        updating = true
        block()
        updating = false
    }

    private fun CharSequence.lastOrNull(): Char? =
        if (isNotEmpty()) last() else null

}