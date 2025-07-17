package io.smileyjoe.applist.drawable

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable


class IconLetter(text: String) : Drawable() {

    private val letter = text.substring(0, 2).uppercase()

    private val shapePaint = Paint().apply {
        isAntiAlias = true
        color = io.smileyjoe.library.utils.Color.Value(text).muted
    }

    private val textPaint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        color = Color.WHITE
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    override fun draw(canvas: Canvas) {
        drawBackground(canvas)
        drawLetter(canvas)
    }

    private fun drawBackground(canvas: Canvas) {
        val height = bounds.height()
        val width = bounds.width()
        val rect = RectF(0.0f, 0.0f, width.toFloat(), height.toFloat())
        canvas.drawRoundRect(rect, rect.bottom, rect.right, shapePaint)
    }

    private fun drawLetter(canvas: Canvas) {
        val rect = Rect()
        val height = bounds.height()
        val width = bounds.width()
        textPaint.textSize = height / 2f
        textPaint.getTextBounds(letter, 0, letter.length, rect)
        val x: Float = width / 2f
        val y: Float = (height + rect.height()) / 2f
        canvas.drawText(letter, x, y, textPaint)
    }

    override fun setAlpha(alpha: Int) {
        shapePaint.alpha = alpha
        textPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        // do nothing, we set the color
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
}