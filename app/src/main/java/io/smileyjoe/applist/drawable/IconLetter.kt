package io.smileyjoe.applist.drawable

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import io.smileyjoe.library.utils.Color

/**
 * Drawable that shows a circular coloured background with the first letters of the provided text.
 *
 * The background color is generated from the provided text, so it will always be the same, it
 * is not random. The [Color] util is used to get the font color.
 *
 * @param text full text that the image relates to
 * @param characters number of characters to show
 * todo: Currently the textsize and positioning is only tested with the specific use case, this should probably be more dynamic. Something like cycle text sizes until it fits
 */
class IconLetter(
    text: String,
    characters: Int = 2
) : Drawable() {

    // letters to show //
    private val letter = text.substring(0, characters).uppercase()

    // color instance used for the background and font //
    private val color = Color.from(text).get()

    // color for the background //
    val backgroundColor: Int = color.main.original

    // paint for the background //
    private val shapePaint = Paint().apply {
        isAntiAlias = true
        color = backgroundColor
    }

    // paint for the text //
    private val textPaint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        color = this@IconLetter.color.title.original
    }

    /**
     * @see [Drawable.draw]
     */
    override fun draw(canvas: Canvas) {
        drawBackground(canvas)
        drawLetter(canvas)
    }

    /**
     * Draw a circular coloured background
     *
     * @param canvas
     */
    private fun drawBackground(canvas: Canvas) {
        val height = bounds.height()
        val width = bounds.width()
        val rect = RectF(0.0f, 0.0f, width.toFloat(), height.toFloat())
        canvas.drawRoundRect(rect, rect.bottom, rect.right, shapePaint)
    }

    /**
     * Draw the letters on top of the background
     *
     * @param canvas
     */
    private fun drawLetter(canvas: Canvas) {
        val rect = Rect()
        val height = bounds.height()
        val width = bounds.width()
        textPaint.textSize = height / 2f
        textPaint.getTextBounds(letter, 0, letter.length, rect)
        // position it in the center //
        val x: Float = width / 2f
        val y: Float = (height + rect.height()) / 2f
        canvas.drawText(letter, x, y, textPaint)
    }

    /**
     * @see [Drawable.setAlpha]
     */
    override fun setAlpha(alpha: Int) {
        shapePaint.alpha = alpha
        textPaint.alpha = alpha
    }

    /**
     * @see [Drawable.setColorFilter]
     */
    override fun setColorFilter(colorFilter: ColorFilter?) {
        // do nothing, we set the color
    }

    /**
     * @see [Drawable.getOpacity]
     */
    override fun getOpacity(): Int {
        // don't know ... but it works //
        return PixelFormat.TRANSLUCENT
    }
}