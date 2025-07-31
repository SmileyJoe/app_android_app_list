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
 * Color.from(imageView).dark(true).get { color ->
 *      // do things with the color palette from the imageView
 * }
 * '''
 */
class Color private constructor(
    palette: Palette,
    useDark: Boolean
) {

    /**
     * Helps provide updated colors such as a [muted] value
     *
     * @param original the original color
     */
    class Value(@ColorInt val original: Int, useDark: Boolean) {

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

        /**
         * Lightened color, this is more of a whitewashed color, 100 lightness and 70% saturation
         */
        val light: Int = cloneOf {
            updateLightness(100)
            updateSaturation(70)
            toColor()
        }

        /**
         * Theme dependant color, if [useDark] is true, return [dim] else return [light]
         */
        val theme: Int =
            if (useDark) dim else light

        /**
         * Inverse of theme dependant color, if [useDark] is true, return [light] else return [dim]
         */
        val themeInverse: Int =
            if (useDark) light else dim
    }

    /**
     * Color builder, multiple [from] methods are provided to make this easier to use.
     *
     * @param builder
     */
    class Builder(
        private val builder: Palette.Builder
    ) {

        // use the dark colors or not //
        private var useDark: Boolean = true

        init {
            builder.maximumColorCount(32)
        }

        /**
         * Use dark colors when using the [Value.theme] or [Value.themeInverse] colors
         *
         * @param useDark
         */
        fun dark(useDark: Boolean) =
            apply { this.useDark = useDark }

        /**
         * Get the [Color] instance in a background thread
         *
         * @param callback
         */
        fun get(callback: ((Color) -> Unit)) {
            builder.generate { palette ->
                palette?.getSwatch()?.let {
                    callback(Color(palette, useDark))
                }
            }
        }

        /**
         * Get the [Color] instance
         */
        fun get(): Color = Color(builder.generate(), useDark)
    }

    companion object {
        /**
         * Get the colors based on the image set to the [imageView]
         *
         * @param imageView
         * @return instance of [Builder]
         */
        fun from(imageView: ImageView) =
            from(imageView.drawable.toBitmap())

        /**
         * Get the colors based on the image set to the [bitmap]
         *
         * @param bitmap
         * @return instance of [Builder]
         */
        fun from(bitmap: Bitmap) =
            Builder(Palette.Builder(bitmap))

        /**
         * Get the colors based on the given [color]
         *
         * @param color
         * @return instance of [Builder]
         */
        fun from(color: Int) =
            from(listOf(color))

        /**
         * Get the colors based on the provided list of colors
         *
         * @param colors
         * @return instance of [Builder]
         */
        fun from(colors: List<Int>) =
            Builder(
                Palette.Builder(colors.map {
                    Swatch(it, 100)
                })
            )

        /**
         * Use the [text] to generate colors
         *
         * @param text
         * @return instance of [Builder]
         */
        fun from(text: String) =
            from(text.toColor())

        /**
         * Get a [Swatch] value from the [Palette], this will check for nulls and return a non
         * null based on the following priority:
         *
         * - dominantSwatch
         * - vibrantSwatch
         * - mutedSwatch
         * - darkMutedSwatch
         * - darkVibrantSwatch
         *
         * @return the color data, or null
         */
        private fun Palette.getSwatch(): Swatch? =
            dominantSwatch ?: vibrantSwatch ?: mutedSwatch ?: darkMutedSwatch ?: darkVibrantSwatch

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
    val main: Value = Value(swatch.rgb, useDark)

    /**
     * A color for any body text that is on top of the [main] color
     */
    val body: Value = Value(swatch.bodyTextColor, useDark)

    /**
     * A color for any title text that is on top of the [main] color
     */
    val title: Value = Value(swatch.titleTextColor, useDark)
}