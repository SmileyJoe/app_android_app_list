package io.smileyjoe.applist.util

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Palette.Swatch

/**
 * Wrapper for helping with [Palette]
 *
 * Usage:
 * '''
 * Color.from(imageView){ color ->
 *      // do things with the color palette from the imageView
 * }
 * '''
 */
class Color private constructor(
    private val palette: Palette
) {

    /**
     * Helps provide updated colors such as a [muted] value
     *
     * @param original the original color
     */
    class Value(@ColorInt val original: Int) {

        /**
         * Muted, or desaturated value, set to 30% saturation
         */
        val muted: Int
            get() {
                val hsl = FloatArray(3)
                ColorUtils.colorToHSL(original, hsl)
                hsl.set(1, (hsl.get(1) * 0.3).toFloat())
                return ColorUtils.HSLToColor(hsl)
            }
    }

    companion object {
        /**
         * Get the colors based on the image set to the [imageView]
         *
         * @param imageView
         * @param color callback with the populated color, this is only called if [Palette] managed to get swatches
         */
        fun from(imageView: ImageView, color: (Color) -> Unit) =
            from(imageView.drawable.toBitmap(), color)

        /**
         * Get the colors based on the image set to the [bitmap]
         *
         * @param bitmap
         * @param color callback with the populated color, this is only called if [Palette] managed to get swatches
         */
        fun from(bitmap: Bitmap, color: (Color) -> Unit) {
            Palette
                .Builder(bitmap)
                .maximumColorCount(32)
                .generate { palette ->
                    palette?.getSwatch()?.let {
                        color(Color(palette))
                    }
                }
        }

        /**
         * Get a [Swatch] value from the [Palette], this will check for nulls and return a non
         * null based on the following priority:
         *
         * - mutedSwatch
         * - darkMutedSwatch
         * - vibrantSwatch
         * - darkVibrantSwatch
         * - dominantSwatch
         *
         * @return the color data, or null
         */
        private fun Palette.getSwatch(): Swatch? =
            mutedSwatch ?: darkMutedSwatch ?: vibrantSwatch ?: darkVibrantSwatch ?: dominantSwatch

    }

    private val swatch: Swatch = palette.getSwatch()!!
    val main: Value = Value(swatch.rgb)
    val body: Value = Value(swatch.bodyTextColor)
    val title: Value = Value(swatch.titleTextColor)
}