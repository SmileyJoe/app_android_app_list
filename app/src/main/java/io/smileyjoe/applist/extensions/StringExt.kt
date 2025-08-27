package io.smileyjoe.applist.extensions

import android.content.Context
import android.content.res.Resources
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import androidx.annotation.ColorInt
import io.smileyjoe.library.utils.IntExt.max
import io.smileyjoe.library.utils.IntExt.min

object StringExt {

    /**
     * Get all the index's of the instance of [text] from the [start] index
     *
     * @param text to look for
     * @param start index to look from, defaults to 0
     * @return a list of all the index positions of the given [text]
     */
    fun String.indexOfAll(text: String, start: Int = 0): List<Int> {
        var index = indexOf(text, start, ignoreCase = true)
        val list = ArrayList<Int>()
        // while an index is found, add it to the list and look again, starting at that point
        while (index > -1) {
            list.add(index)
            index = indexOf(text, index + 1, ignoreCase = true)
        }
        return list
    }

    /**
     * Highlight the provided [text] with the [color]
     *
     * @param text to highlight
     * @param color to use
     * @return ths string with the highlighted [text]
     */
    fun String.highlight(text: String?, @ColorInt color: Int): SpannableString {
        val span = SpannableString(this)
        if (!text.isNullOrEmpty()) {
            indexOfAll(text).forEach { start ->
                val end = start + text.length
                span.setSpan(
                    BackgroundColorSpan(color),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        return span
    }

    /**
     * Get a summary using the [text] as the focal point, will return the [count] characters
     * before and after the first occurrence of the [text]
     *
     * @param text to look for
     * @param count number of characters to show before and after
     * @param ellipsize show ... if any text was cut off
     * @return summary
     */
    fun String.summary(text: String?, count: Int, ellipsize: Boolean = false): String {
        if (!text.isNullOrEmpty()) {
            val index = indexOf(text)
            val start = (index - count).min(0)
            val end = (index + text.length + count).times(if (index <= 0) 2 else 1).max(length)
            return substring(start, end)
                .ellipsize(
                    start = ellipsize && start > 0,
                    end = ellipsize && end < length
                )
        } else {
            return this
        }
    }

    /**
     * Add ... to the start and end of the string
     *
     * @param start
     * @param end
     * @return ellipsized string
     */
    fun String.ellipsize(start: Boolean = false, end: Boolean = false): String {
        val ellipse = "..."
        val startEllipse = if (start) ellipse else ""
        val endEllipse = if (end) ellipse else ""
        return "$startEllipse$this$endEllipse"
    }

    /**
     * Remove any line breaks and multi spaces
     */
    fun String.removeBreaks() =
        replace("\n", " ")
            .replace("\\s+".toRegex(), " ")

    /**
     * Get the text value of a string res id
     *
     * @param context
     * @return the string value, or null
     */
    fun Int.fromRes(context: Context): String? =
        try {
            context.getString(this)
        } catch (e: Resources.NotFoundException) {
            null
        }

}