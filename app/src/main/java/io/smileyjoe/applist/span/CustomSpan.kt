package io.smileyjoe.applist.span

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan
import kotlin.math.roundToInt

/**
 * Helper for using [ReplacementSpan], keeps a record of all the info provided by the
 * [ReplacementSpan.draw] function, so that logic can easily be split without having to
 * pass a bunch of data between functions.
 *
 * Also handled [ReplacementSpan.getSize]
 *
 * @see details all the data needed to draw the span
 */
abstract class CustomSpan : ReplacementSpan() {

    /**
     * All the data provided by the [ReplacementSpan.draw] function
     *
     * @see ReplacementSpan.draw for any param details
     */
    protected data class Details(
        val canvas: Canvas,
        val text: CharSequence?,
        val start: Int,
        val end: Int,
        val x: Float,
        val top: Int,
        val y: Int,
        val bottom: Int,
        val paint: Paint
    ) {
        val content = text?.substring(start, end)
    }

    private var _details: Details? = null
    protected val details
        get() = _details!!

    /**
     * Draw the span, [details] will be populated at this point
     */
    abstract fun draw()

    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int = paint.measureText(text, start, end).roundToInt()

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        _details = Details(
            canvas = canvas,
            text = text,
            start = start,
            end = end,
            x = x,
            top = top,
            y = y,
            bottom = bottom,
            paint = paint
        )

        draw()
    }

    /**
     * Measure the provided [text], or a subset if [start] and [end] is specified
     *
     * @param paint that will be used to draw the text
     * @param text to measure
     * @param start position, defaults to the start of the [text]
     * @param end position, defaults to the end of the [text]
     */
    protected fun measureText(
        paint: Paint,
        text: CharSequence,
        start: Int = 0,
        end: Int = text.length
    ) =
        paint.measureText(text, start, end)
}