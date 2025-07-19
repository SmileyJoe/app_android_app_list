package io.smileyjoe.library.utils

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Palette.Swatch
import io.smileyjoe.library.utils.Extensions.toColor

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
         * Update the hue, hue represents WHAT the color is
         *
         * @param percent
         * @see update
         */
        private fun FloatArray.updateHue(percent: Int) =
            update(0, percent)

        /**
         * Update the saturation, this represents how much of the color is used
         *
         * @param percent
         * @see update
         */
        private fun FloatArray.updateSaturation(percent: Int) =
            update(1, percent)

        /**
         * Update the lightness, or brightness
         *
         * @param percent
         * @see update
         */
        private fun FloatArray.updateLightness(percent: Int) =
            update(2, percent)

        /**
         * Update a specific value
         *
         * @param position in the [hsl] array
         * @param percent to change it by
         */
        private fun FloatArray.update(position: Int, percent: Int) =
            set(position, get(position) * (percent.toFloat() / 100))

        /**
         * All new values are clones of the originals [hsl] representation, this will
         * make a clone of that and return it in the provided [block]
         *
         * @param block callback with a clone of the [hsl] value
         */
        private fun <R> cloneOf(block: FloatArray.() -> R): R {
            return hsl.clone().block()
        }

        /**
         * Convert the [hsl], or cloned and edited value, to a [ColorInt]
         */
        @ColorInt
        private fun FloatArray.toColor() =
            ColorUtils.HSLToColor(this)

        /**
         * Hue, Saturation, Lightness representation of the color
         */
        private val hsl = with(FloatArray(3)) {
            ColorUtils.colorToHSL(original, this)
            this
        }

        /**
         * Muted, or desaturated value, set to 30% saturation
         */
        val muted: Int = cloneOf {
            updateSaturation(30)
            toColor()
        }

        /**
         * Dimmed color at 20% lightness
         */
        val dim: Int = cloneOf {
            updateLightness(20)
            toColor()
        }
    }

    companion object {
        /**
         * Get the colors based on the image set to the [imageView]
         *
         * @param imageView
         * @param color callback with the populated color, this is only called if [Palette] managed to get swatches
         */
        fun from(imageView: ImageView, onGenerated: ((Color) -> Unit)? = null) =
            from(imageView.drawable.toBitmap(), onGenerated)

        /**
         * Get the colors based on the image set to the [bitmap]
         *
         * @param bitmap
         * @param color callback with the populated color, this is only called if [Palette] managed to get swatches
         */
        fun from(bitmap: Bitmap, onGenerated: ((Color) -> Unit)? = null) =
            from(Palette.Builder(bitmap), onGenerated)

        fun from(color: Int, onGenerated: ((Color) -> Unit)? = null) =
            from(listOf(color), onGenerated)

        fun from(colors: List<Int>, onGenerated: ((Color) -> Unit)? = null): Color? =
            from(
                builder = Palette.Builder(colors.map {
                    Swatch(it, 100)
                }),
                onGenerated = onGenerated
            )

        fun from(text: String, onGenerated: ((Color) -> Unit)? = null) =
            from(listOf(text.toColor()), onGenerated)

        private fun from(builder: Palette.Builder, onGenerated: ((Color) -> Unit)? = null): Color? {
            builder
                .maximumColorCount(32)

            return onGenerated?.let {
                builder.generate { palette ->
                    palette?.getSwatch()?.let {
                        onGenerated(Color(palette))
                    }
                }
                return null
            } ?: run {
                return Color(builder.generate())
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

        /**
         * Convert the [ColorInt], to a [ColorStateList]
         */
        fun Int.toColorStateList() =
            ColorStateList.valueOf(this)

    }

    private val swatch: Swatch = palette.getSwatch()!!

    /**
     * The main color, normally used for backgrounds etc
     */
    val main: Value = Value(swatch.rgb)

    /**
     * A color for any body text that is on top of the [main] color
     */
    val body: Value = Value(swatch.bodyTextColor)

    /**
     * A color for any title text that is on top of the [main] color
     */
    val title: Value = Value(swatch.titleTextColor)
}