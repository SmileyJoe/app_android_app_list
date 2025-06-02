package io.smileyjoe.applist.extensions

import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import io.smileyjoe.applist.extensions.IntExt.max
import io.smileyjoe.applist.extensions.IntExt.min

object StringExt {

    fun String.indexOfAll(text: String, start: Int = 0): List<Int> {
        var index = indexOf(text, start)
        val list = ArrayList<Int>()
        while (index > -1) {
            list.add(index)
            index = indexOf(text, index + 1)
        }
        return list
    }

    fun String.highlight(text: String?, color: Int): SpannableString {
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

    fun String.summary(text: String?, count: Int, ellipsize: Boolean = false): String {
        if (!text.isNullOrEmpty()) {
            val index = indexOf(text)
            val start = (index - count).min(0)
            val end = (index + text.length + count).max(length)
            return substring(start, end)
                .ellipsize(
                    start = ellipsize && start > 0,
                    end = ellipsize && end < length
                )
        } else {
            return this
        }
    }

    fun String.ellipsize(start: Boolean = false, end: Boolean = false): String {
        val ellipse = "..."
        val startEllipse = if (start) ellipse else ""
        val endEllipse = if (end) ellipse else ""
        return "$startEllipse$this$endEllipse"
    }

    fun String.removeBreaks() =
        replace("\n", " ")
            .replace("\\s+".toRegex(), " ")

}