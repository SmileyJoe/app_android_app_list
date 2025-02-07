package io.smileyjoe.applist.span

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.color.MaterialColors
import io.smileyjoe.applist.R

/**
 * Span that adds a chip style background to the provided text, also includes a remove icon before
 * the text.
 *
 * @param context
 * @param theme details for the span
 * @param height fix the height of the span, if null it will be calculated internally
 * @param width fix the width of the span, if null it will be calculated internally
 * @param onMeasure callback with the text in the span, as well as it's width and height
 * todo: Make the remove icon optional
 */
class ChipSpan(
    context: Context,
    private val theme: Theme,
    private var height: Int? = null,
    private var width: Float? = null,
    private val onMeasure: ((content: String?, height: Int, width: Float) -> Unit)? = null
) : CustomSpan() {

    /**
     * Theming details for the span
     *
     * @param context
     * @param tint of the chip, defaults to colorSecondaryContainer
     * @param removeIconTint color for the remove icon, defaults to colorAccent
     * @param textColor defaults to colorAccent
     * @param paddingTop add some space above the text, inside of the span, defaults to 0
     * @param paddingBottom add some space below the text, inside of the span, defaults to 0
     */
    class Theme(
        context: Context,
        @ColorInt tint: Int? = null,
        @ColorInt removeIconTint: Int? = null,
        @ColorInt textColor: Int? = null,
        val paddingTop: Int = 0,
        val paddingBottom: Int = 0
    ) {
        @ColorInt
        private val colorSecondaryContainer = MaterialColors.getColor(
            context,
            R.attr.colorSecondaryContainer,
            Color.WHITE
        )

        @ColorInt
        private val colorAccent = MaterialColors.getColor(context, R.attr.colorAccent, Color.WHITE)

        @ColorInt
        val tint = tint ?: colorSecondaryContainer

        @ColorInt
        val removeIconTint = removeIconTint ?: colorAccent

        @ColorInt
        val textColor = textColor ?: colorAccent
    }

    private val removeIcon: Bitmap? =
        ContextCompat.getDrawable(context, R.drawable.ic_close)?.toBitmap()

    override fun draw() {
        // only draw everything if there is text //
        if (!details.content.isNullOrBlank()) {
            measure()
            drawChip()
            drawText()
            drawRemoveIcon()
        }
    }

    /**
     * Calculate all the measurements of the span
     */
    private fun measure() {
        with(details) {
            // if the provided height is null, calculate it and set it
            height = height ?: ((bottom) - (top - theme.paddingTop))
            // if the provided width is null, measure the text and set it
            width = width ?: measureText(
                paint,
                content!!
            )
            // return the details to the caller
            onMeasure?.invoke(content, height!!, width!!)
        }
    }

    /**
     * Draw the chip
     */
    private fun drawChip() {
        with(details) {
            val rect = RectF(
                x,
                top.toFloat() - theme.paddingTop,
                x + width!!,
                top.toFloat() + height!!
            )
            paint.color = theme.tint

            canvas.drawRoundRect(rect, rect.height() / 2, rect.height() / 2, paint)
        }
    }

    /**
     * Draw the text
     */
    private fun drawText() {
        with(details) {
            paint.color = theme.textColor
            canvas.drawText(
                text!!,
                start,
                end,
                x,
                y.toFloat(),
                paint
            )
        }
    }

    /**
     * Draw the remove icon
     */
    private fun drawRemoveIcon() {
        with(details) {
            paint.setColorFilter(
                PorterDuffColorFilter(
                    theme.removeIconTint,
                    PorterDuff.Mode.SRC_IN
                )
            )
            removeIcon?.let {
                canvas.drawBitmap(
                    it,
                    x + measureText(paint, "  "),
                    top.toFloat(),
                    paint
                )
            }
        }
    }
}