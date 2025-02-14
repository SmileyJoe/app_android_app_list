package io.smileyjoe.library.tags

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import androidx.annotation.XmlRes
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.textfield.TextInputEditText
import io.smileyjoe.library.utils.CustomTextWatcher
import io.smileyjoe.library.utils.Extensions.edit
import io.smileyjoe.library.utils.Extensions.removeSpans
import io.smileyjoe.library.utils.Extensions.withNotNull

/**
 * Text watcher to use on a [TextInputEditText] to allow the user to add tags.
 *
 * Tags a separated by a space
 *
 * @param context
 * @see ChipSpan
 */
class TagTextWatcher(
    private val context: Context,
    @XmlRes private val chipXml: Int
) : CustomTextWatcher() {

    companion object {
        const val REMOVE_NONE = -1
        const val REMOVE_LAST = -2
    }

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
            if (isNotEmpty() && lastChar == ' ' && !updating) {
                // when replacing the text in the editable, the spans are left, as we are replacing //
                // everything, just remove all instances of the spans we added //
                update {
                    removeSpans(ClickPositionSpan::class.java)
                    removeSpans(ImageSpan::class.java)
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
                // make sure all the tags are distinct //
                .distinct()
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
        ImageSpan(ChipDrawable.createFromResource(context, chipXml).apply {
            this.text = text
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        })
}