package io.smileyjoe.applist.textwatcher

import android.content.Context
import android.text.SpannableStringBuilder
import com.google.android.material.textfield.TextInputEditText
import io.smileyjoe.applist.extensions.Extensions.edit
import io.smileyjoe.applist.extensions.Extensions.pad
import io.smileyjoe.applist.extensions.Extensions.removeSpans
import io.smileyjoe.applist.extensions.Extensions.withNotNull
import io.smileyjoe.applist.span.ChipSpan
import io.smileyjoe.applist.span.ClickPositionSpan

/**
 * Text watcher to use on a [TextInputEditText] to allow the user to add tags.
 *
 * Tags a separated by a space
 *
 * @param context
 * @param chipTheme styling details of the span used for each tag
 * @see ChipSpan
 */
class TagTextWatcher(
    private val context: Context,
    private val chipTheme: ChipSpan.Theme
) : CustomTextWatcher() {

    companion object {
        const val REMOVE_NONE = -1
        const val REMOVE_LAST = -2
    }

    /**
     * Record the height of the chip, every chip will have the same height so this just saves some
     * calculations. It also caters for adding extra line spacing making the tag to big when
     * they start to wrap on a new line
     */
    private var chipHeight: Int? = null

    /**
     * Keep a record of the chip widths, when a [TextInputEditText] uses multiple line, it trims the
     * end of each line, which is problematic as spaces are used as padding for each span.
     *
     * By keeping track of the widths, when a new line is added, we can give the span at the end
     * of each line the measured width before it was trimmed
     */
    private val chipWidths: MutableMap<String, Float> = mutableMapOf()

    override fun afterTextChanged() {
        updateText(
            if (direction == Direction.BACKWARD) REMOVE_LAST else REMOVE_NONE
        )
    }

    /**
     * Update the text
     *
     * @param remove position of span to remove
     * @see REMOVE_LAST
     * @see REMOVE_NONE
     */
    private fun updateText(remove: Int) {
        withNotNull(editable) {
            // only change anything if there is content, and the change is coming from an internal update //
            if (isNotEmpty() && last() == ' ' && !updating) {
                // when replacing the text in the editable, the spans are left, as we are replacing //
                // everything, just remove all instances of the spans we added //
                update {
                    removeSpans(ChipSpan::class.java)
                    removeSpans(ClickPositionSpan::class.java)
                }
                // replace the contents if there is new content //
                addSpans(remove)?.let {
                    update {
                        replace(
                            0,
                            length,
                            it
                        )
                    }
                } ?: run {
                    // clear everything if there is no content //
                    clear()
                }
            }
        }
    }

    /**
     * Add the spans to the text in the [editable]
     *
     * @param remove position of span to remove
     */
    private fun addSpans(remove: Int): SpannableStringBuilder? =
        withNotNull(editable) {
            val builder = SpannableStringBuilder()
            // get each tag //
            split(" ")
                // remove all blank words //
                .filter { it.isNotBlank() }
                // allow the list to be edited //
                .toMutableList()
                // if there is something to remove, remove it //
                .edit(remove != REMOVE_NONE) {
                    removeAt(
                        if (remove == REMOVE_LAST) lastIndex else remove
                    )
                }
                // add the space prefix and suffix that is used as padding //
                .map {
                    it.pad(start = 8, end = 4, char = ' ')
                }
                // cycle each tag and everything to the builder //
                .forEachIndexed { index, text ->
                    builder.apply {
                        val start = length
                        append(text, getChipSpan(text), 0)
                        setSpan(ClickPositionSpan(index) {
                            updateText(it)
                        }, start, builder.length, 0)
                        append(" ")
                    }
                }
            return@withNotNull builder
        }

    /**
     * Get the chip span for the provided [text]
     *
     * @param text that will be in the span
     */
    private fun getChipSpan(text: String) =
        ChipSpan(
            context = context,
            height = chipHeight,
            width = chipWidths[text],
            theme = chipTheme
        ) { content, height, width ->
            chipHeight = height
            content?.let { chipWidths[it] = width }
        }
}